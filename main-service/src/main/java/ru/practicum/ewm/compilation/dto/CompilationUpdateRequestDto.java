package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationUpdateRequestDto {
    Set<Long> eventIds;
    Boolean pinned;

    @Size(max = 50)
    String title;
}