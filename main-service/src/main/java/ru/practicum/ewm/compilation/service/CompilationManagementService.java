package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequestDto;

public interface CompilationManagementService {
    CompilationResponseDto createCompilation(CompilationRequestDto request);

    CompilationResponseDto updateCompilation(Long compilationId, CompilationUpdateRequestDto update);

    void deleteCompilation(Long compilationId);
}