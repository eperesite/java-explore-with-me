package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event объект не может быть null");
        }
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryOutDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event объект не может быть null");
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryOutDto(event.getCategory()))
                .createdOn(event.getCreatedDate())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublisherDate())
                .requestModeration(event.isRequestModeration())
                .state(event.getEventStatus())
                .title(event.getTitle())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto) {
        if (newEventDto == null) {
            throw new IllegalArgumentException("NewEventDto объект не может быть null");
        }
        String trimmedTitle = newEventDto.getTitle() != null ? newEventDto.getTitle().trim() : "";

        if (trimmedTitle.isEmpty()) {
            throw new IllegalArgumentException("Название события не может быть пустым или состоять из пробелов");
        }

        return Event.builder()
                .id(null)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(trimmedTitle)
                .build();
    }

    public static List<EventShortDto> eventToEventShortDtoList(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}
