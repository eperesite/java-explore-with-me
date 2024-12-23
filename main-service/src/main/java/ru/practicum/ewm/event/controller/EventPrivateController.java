package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventManagementService;
import ru.practicum.ewm.request.service.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventManagementService eventService;

    @GetMapping
    public List<EventShortDto> getAllByUserId(@PathVariable("userId") @Min(1) Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Fetching events for user with ID: {}", userId);
        return eventService.getByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable("userId") @Min(1) Long userId,
                               @RequestBody @Valid NewEventDto eventDto) {
        log.info("Creating event for user with ID: {}", userId);
        return eventService.create(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserIdAndEventId(@PathVariable("userId") @Min(1) Long userId,
                                              @PathVariable("eventId") @Min(1) Long eventId) {
        log.info("Fetching event with ID: {} for user with ID: {}", eventId, userId);
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByUserIdAndEventId(@PathVariable("userId") @Min(1) Long userId,
                                                 @PathVariable("eventId") @Min(1) Long eventId,
                                                 @RequestBody @Valid UpdateEventUserRequest update) {
        log.info("Updating event with ID: {} for user with ID: {}", eventId, userId);
        return eventService.updateByUserIdAndEventId(userId, eventId, update);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsByEventId(@PathVariable("userId") @Min(1) Long userId,
                                                                 @PathVariable("eventId") @Min(1) Long eventId) {
        log.info("Fetching requests for event with ID: {} for user with ID: {}", eventId, userId);
        return eventService.getAllParticipationRequestsByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public Map<String, List<ParticipationRequestDto>> approveRequests(@PathVariable("userId") Long userId,
                                                                      @PathVariable("eventId") Long eventId,
                                                                      @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.info("Updating requests for event with ID: {} for user with ID: {}", eventId, userId);
        return eventService.approveRequests(userId, eventId, request);
    }
}