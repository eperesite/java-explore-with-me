package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflictException;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = checkUser(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        LocalDateTime createdAt = LocalDateTime.now();
        validateNewRequest(event, userId, eventId);

        Request request = new Request();
        request.setCreatedAt(createdAt);
        request.setRequester(user);
        request.setEvent(event);

        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        requestRepository.save(request);

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findByRequestIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with ID " + requestId + " not found"));

        if (request.getStatus() == RequestStatus.CANCELED || request.getStatus() == RequestStatus.REJECTED) {
            throw new ValidationConflictException("Request is not confirmed");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(updatedRequest);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    }

    private void validateNewRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getUserId().equals(userId)) {
            throw new ValidationConflictException("User with ID " + userId + " is the event initiator");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ValidationConflictException("Participant limit exceeded for event with ID " + eventId);
        }
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationConflictException("Event with ID " + eventId + " is not published");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ValidationConflictException("Duplicate request for event with ID " + eventId);
        }
    }
}