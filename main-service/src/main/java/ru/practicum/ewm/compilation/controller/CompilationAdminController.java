package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequestDto;
import ru.practicum.ewm.compilation.service.CompilationService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@RequestBody @Valid CompilationRequestDto compilationDto) {
        log.info("Запрос на создание подборки событий");
        return compilationService.createCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(@RequestBody @Valid CompilationUpdateRequestDto update,
                                                    @PathVariable Long compId) {
        log.info("Запрос на обнавление подборки событий");
        return compilationService.updateCompilation(compId, update);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Запрос на удаление подборки событий");
        compilationService.deleteCompilation(compId);
    }

}