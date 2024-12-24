package ru.practicum.ewm.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static CompilationResponseDto toDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public  static Compilation toCompilation(CompilationRequestDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }
}