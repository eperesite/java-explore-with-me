package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequestDto;

import java.util.List;

public interface CompilationService {
    CompilationResponseDto createCompilation(CompilationRequestDto compilationDto);

    CompilationResponseDto updateCompilation(Long compId, CompilationUpdateRequestDto update);

    void deleteCompilation(Long compId);

    List<CompilationResponseDto> getCompilation(Boolean pinned, Integer from, Integer size);

    CompilationResponseDto findCompilationById(Long compId);
}