package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.constants.DateTimeFormatConstants;
import ru.practicum.ewm.user.dto.UserShortResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    CategoryResponseDto category;
    Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatConstants.DATE_TIME_PATTERN)
    LocalDateTime eventDate;

    UserShortResponseDto initiator;
    Boolean paid;
    String title;
    Long views;
    Long comments;
}