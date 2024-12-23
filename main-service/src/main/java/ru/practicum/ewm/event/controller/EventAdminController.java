package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventAdminParams;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventManagementService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventManagementService eventService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventFullDto> getAll(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Fetching all events (ADMIN)");
        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, formatter) : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, formatter) : LocalDateTime.now().plusYears(20);

        EventAdminParams params = EventAdminParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(start)
                .rangeEnd(end)
                .from(from)
                .size(size)
                .build();

        return eventService.getAllAdmin(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable("eventId") @Min(1) Long eventId,
                               @RequestBody @Valid UpdateEventAdminRequest update) {
        log.info("Updating event with ID: {}", eventId);
        return eventService.updateAdmin(eventId, update);
    }
}