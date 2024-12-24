package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.service.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

@Service
public interface EventService {
    List<EventFullDto> getAllEventsAdmin(EventAdminParams eventAdminParams);

    EventFullDto updateEventsAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvents(Long userId, NewEventDto newEventDto);

    EventFullDto getEventsByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventsByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest eventUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsByOwner(Long userId, Long eventId);

    List<EventShortDto> getAllEventsPublic(EventParams eventParams, HttpServletRequest request);

    EventFullDto getEventsById(Long eventId, HttpServletRequest request);

    Map<String, List<ParticipationRequestDto>> approveRequests(final Long userId, final Long eventId,
                                                               final EventRequestStatusUpdateRequest requestUpdateDto);

}