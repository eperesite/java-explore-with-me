package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationResponseDto;

import java.util.List;

public interface CompilationQueryService {
    List<CompilationResponseDto> getAllCompilations(Boolean pinned, Integer offset, Integer limit);

    CompilationResponseDto getCompilationById(Long compilationId);
}