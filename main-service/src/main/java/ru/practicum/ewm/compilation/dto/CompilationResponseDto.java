package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.event.dto.EventShortDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponseDto {
    Long id;
    Set<EventShortDto> events;
    Boolean pinned;
    String title;
}