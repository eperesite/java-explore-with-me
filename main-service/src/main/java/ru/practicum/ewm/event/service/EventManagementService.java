package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.service.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface EventManagementService {
    List<EventFullDto> getAllAdmin(EventAdminParams params);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest update);

    List<EventShortDto> getByUserId(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto getByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest update);

    List<ParticipationRequestDto> getAllParticipationRequestsByOwner(Long userId, Long eventId);

    Map<String, List<ParticipationRequestDto>> approveRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}