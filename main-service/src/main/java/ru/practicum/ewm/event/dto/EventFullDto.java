package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.constants.DateTimeFormatConstants;
import ru.practicum.ewm.event.model.EventStatus;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.dto.UserShortResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryResponseDto category;
    Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatConstants.DATE_TIME_PATTERN)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatConstants.DATE_TIME_PATTERN)
    LocalDateTime eventDate;

    UserShortResponseDto initiator;
    Location.LocationDto location;
    Boolean paid;
    Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatConstants.DATE_TIME_PATTERN)
    LocalDateTime publishedOn;

    Boolean requestModeration;
    EventStatus state;
    String title;
    Long views;
}