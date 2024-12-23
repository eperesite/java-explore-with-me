package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatDto;
import ru.practicum.StatOutDto;
import ru.practicum.client.StatisticsClient;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.comment.CommentCountByEventDto;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.service.ParticipationRequestDto;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventManagementService, EventQueryService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final CommentRepository commentRepository;
    private final StatisticsClient statsClient;
    private final ObjectMapper objectMapper;

    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    @Override
    public List<EventFullDto> getAllAdmin(EventAdminParams params) {
        PageRequest pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Specification<Event> specification = Specification.where(null);

        if (params.getUsers() != null && !params.getUsers().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(params.getUsers()));
        }

        if (params.getStates() != null && !params.getStates().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("eventStatus").as(String.class).in(params.getStates()));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(params.getCategories()));
        }

        if (params.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), params.getRangeEnd()));
        }

        if (params.getRangeStart() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), params.getRangeStart()));
        }

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        List<EventFullDto> result = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, List<Request>> confirmedRequestsCountMap = getConfirmedRequestsCount(events);

        for (EventFullDto event : result) {
            List<Request> requests = confirmedRequestsCountMap.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequests(requests.size());
        }

        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest update) {
        Event event = checkEvent(eventId);

        if (event.getEventStatus().equals(EventStatus.PUBLISHED) || event.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new ValidationConflictException("Event with status " + event.getEventStatus() + " cannot be updated");
        }

        Event updatedEvent = updateEventBase(event, update);

        if (update.getEventDate() != null) {
            if (update.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Event date must be at least 1 hour after the current time");
            }
            updatedEvent.setEventDate(update.getEventDate());
        }

        if (update.getStateAction() != null) {
            if (EventAdminState.PUBLISH_EVENT.equals(update.getStateAction())) {
                updatedEvent.setEventStatus(EventStatus.PUBLISHED);
            } else if (EventAdminState.REJECT_EVENT.equals(update.getStateAction())) {
                updatedEvent.setEventStatus(EventStatus.CANCELED);
            }
        }

        eventRepository.save(updatedEvent);

        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getByUserId(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventRepository.findAll(pageRequest).getContent()
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);

        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Event with ID " + eventId + " is not published");
        }

        statsClient.postHit(StatDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Map<Long, Long> viewStatsMap = getViews(List.of(event));
        Long views = viewStatsMap.getOrDefault(event.getId(), 1L);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User user = checkUser(userId);
        checkDateAndTime(LocalDateTime.now(), eventDto.getEventDate());
        Category category = checkCategory(eventDto.getCategory());
        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);

        if (eventDto.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));
            event.setLocation(location);
        }

        Event savedEvent = eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(savedEvent);
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest update) {
        checkUser(userId);
        Event event = checkEventByInitiatorAndEventId(userId, eventId);

        if (event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationConflictException("Event with status " + event.getEventStatus() + " cannot be updated");
        }

        Event updatedEvent = updateEventBase(event, update);

        LocalDateTime newDate = update.getEventDate();

        if (newDate != null) {
            checkDateAndTime(LocalDateTime.now(), newDate);
            updatedEvent.setEventDate(newDate);
        }

        EventUserState stateAction = update.getStateAction();

        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    updatedEvent.setEventStatus(EventStatus.PENDING);
                    break;
                case CANCEL_REVIEW:
                    updatedEvent.setEventStatus(EventStatus.CANCELED);
                    break;
            }
        }

        eventRepository.save(updatedEvent);

        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto getByUserIdAndEventId(Long userId, Long eventId) {
        checkUser(userId);
        Event event = checkEventByInitiatorAndEventId(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsByOwner(Long userId, Long eventId) {
        checkUser(userId);
        checkEventByInitiatorAndEventId(userId, eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllPublic(EventParams params, HttpServletRequest request) {
        if (params.getRangeEnd() != null && params.getRangeStart() != null) {
            if (params.getRangeEnd().isBefore(params.getRangeStart())) {
                throw new ValidationException("End date cannot be before start date");
            }
        }

        statsClient.postHit(StatDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Specification<Event> specification = Specification.where(null);
        LocalDateTime now = LocalDateTime.now();

        if (params.getText() != null) {
            String searchText = params.getText().toLowerCase();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + searchText + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchText + "%")
                    ));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(params.getCategories()));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(params.getRangeStart(), now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (params.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), params.getRangeEnd()));
        }

        if (params.getOnlyAvailable() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("eventStatus"), EventStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        List<EventShortDto> result = resultEvents
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        Map<Long, Long> viewStatsMap = getViews(resultEvents);

        List<CommentCountByEventDto> commentsCountMap = commentRepository.countCommentsByEventIds(
                resultEvents.stream().map(Event::getId).collect(Collectors.toList()));
        Map<Long, Long> commentsCountToEventIdMap = commentsCountMap.stream().collect(Collectors.toMap(
                CommentCountByEventDto::getEventId, CommentCountByEventDto::getCountComments));

        for (EventShortDto event : result) {
            Long viewsFromMap = viewStatsMap.getOrDefault(event.getId(), 0L);
            event.setViews(viewsFromMap);

            Long commentCountFromMap = commentsCountToEventIdMap.getOrDefault(event.getId(), 0L);
            event.setComments(commentCountFromMap);
        }

        return result;
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
    }

    private Event checkEventByInitiatorAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " for user with ID " + userId + " not found"));
    }

    private Category checkCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    }

    private void checkDateAndTime(LocalDateTime currentTime, LocalDateTime eventDate) {
        if (eventDate.isBefore(currentTime.plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours after the current time");
        }
    }

    private Map<Long, Long> getViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<LocalDateTime> startDates = events.stream()
                .map(Event::getCreatedDate)
                .collect(Collectors.toList());
        LocalDateTime earliestDate = startDates.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> viewStatsMap = new HashMap<>();

        if (earliestDate != null) {
            ResponseEntity<Object> response = statsClient.getStatistics(earliestDate.toString(), LocalDateTime.now().toString(), uris, true);
            List<StatOutDto> statOutDtoList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});

            viewStatsMap = statOutDtoList.stream()
                    .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())), StatOutDto::getHits));
        }
        return viewStatsMap;
    }

    private Map<Long, List<Request>> getConfirmedRequestsCount(List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events
                .stream().map(Event::getId).collect(Collectors.toList()), RequestStatus.CONFIRMED);
        return requests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId()));
    }

    private Event updateEventBase(Event event, UpdateEventBase update) {
        if (update.getAnnotation() != null && !update.getAnnotation().isBlank()) {
            event.setAnnotation(update.getAnnotation());
        }

        if (update.getCategory() != null) {
            Category category = checkCategory(update.getCategory());
            event.setCategory(category);
        }

        if (update.getDescription() != null && !update.getDescription().isBlank()) {
            event.setDescription(update.getDescription());
        }

        if (update.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(update.getLocation()));
        }

        if (update.getParticipantLimit() != null) {
            event.setParticipantLimit(update.getParticipantLimit());
        }

        if (update.getPaid() != null) {
            event.setPaid(update.getPaid());
        }

        if (update.getRequestModeration() != null) {
            event.setRequestModeration(update.getRequestModeration());
        }

        if (update.getTitle() != null && !update.getTitle().isBlank()) {
            event.setTitle(update.getTitle());
        }

        return event;
    }

    @Override
    @Transactional
    public Map<String, List<ParticipationRequestDto>> approveRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ValidationConflictException("User is not the initiator of this event");
        }

        List<Request> requests = requestRepository.findAllByRequestIdIn(request.getRequestIds().stream().toList());

        if (event.isRequestModeration() && event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0 && request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationConflictException("Participant limit for this event is reached");
        }

        boolean verified = requests.stream()
                .allMatch(req -> req.getEvent().getId().longValue() == eventId);
        if (!verified) {
            throw new ValidationConflictException("Requests do not belong to the same event");
        }

        Map<String, List<ParticipationRequestDto>> requestMap = new HashMap<>();

        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            if (requests.stream()
                    .anyMatch(req -> req.getStatus().equals(RequestStatus.CONFIRMED))) {
                throw new ValidationConflictException("Cannot reject confirmed requests");
            }
            log.info("Rejecting requests");

            List<ParticipationRequestDto> rejectedRequests = requests.stream()
                    .peek(req -> req.setStatus(RequestStatus.REJECTED))
                    .map(requestRepository::save)
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);
        } else {
            if (requests.stream()
                    .anyMatch(req -> !req.getStatus().equals(RequestStatus.PENDING))) {
                throw new ValidationConflictException("Requests must be in PENDING status");
            }
            Integer confirmedRequestsCount = 0;

            if (event.getConfirmedRequests() != null) {
                confirmedRequestsCount = event.getConfirmedRequests();
            }

            long limit = event.getParticipantLimit() - confirmedRequestsCount;
            List<Request> confirmedList = requests.stream()
                    .limit(limit)
                    .peek(req -> req.setStatus(RequestStatus.CONFIRMED))
                    .map(requestRepository::save).toList();
            log.info("Confirmed requests saved");

            List<ParticipationRequestDto> confirmedRequests = confirmedList.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("confirmedRequests", confirmedRequests);

            List<Request> rejectedList = requests.stream()
                    .skip(limit)
                    .peek(req -> req.setStatus(RequestStatus.REJECTED))
                    .map(requestRepository::save).toList();
            log.info("Rejected requests saved due to limit");
            List<ParticipationRequestDto> rejectedRequests = rejectedList.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);

            if (event.getConfirmedRequests() != null) {
                confirmedRequestsCount = event.getConfirmedRequests();
            }
            event.setConfirmedRequests(confirmedList.size() + confirmedRequestsCount);
            eventRepository.save(event);
        }
        return requestMap;
    }
}