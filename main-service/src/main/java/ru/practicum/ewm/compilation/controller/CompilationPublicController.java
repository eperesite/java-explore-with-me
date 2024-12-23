package ru.practicum.ewm.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.service.CompilationQueryService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationQueryService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponseDto> getAll(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        log.info("Fetching compilations with pinned={}, offset={}, limit={}", pinned, offset, limit);
        return compilationService.getAllCompilations(pinned, offset, limit);
    }

    @GetMapping("/{compId}")
    public CompilationResponseDto getById(@PathVariable Long compilationId) {
        log.info("Fetching compilation with ID: {}", compilationId);
        return compilationService.getCompilationById(compilationId);
    }
}