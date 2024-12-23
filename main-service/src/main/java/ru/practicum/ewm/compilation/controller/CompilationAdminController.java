package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequestDto;
import ru.practicum.ewm.compilation.service.CompilationManagementService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationManagementService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto create(@RequestBody @Valid CompilationRequestDto request) {
        log.info("Creating a new compilation: {}", request);
        return compilationService.createCompilation(request);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto update(@PathVariable Long compilationId,
                                         @RequestBody @Valid CompilationUpdateRequestDto update) {
        log.info("Updating compilation with ID: {}", compilationId);
        return compilationService.updateCompilation(compilationId, update);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compilationId) {
        log.info("Deleting compilation with ID: {}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }
}