package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventParams;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

public interface EventQueryService {
    List<EventShortDto> getAllPublic(EventParams params, HttpServletRequest request);

    EventFullDto getById(Long eventId, HttpServletRequest request);
}