package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.service.CategoryService;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class  CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        log.info("Создание новой категории: {}", categoryRequestDto);
        return categoryService.createCategory(categoryRequestDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "catId") Long id) {
        log.info("Удаление категории с идентификатором: {}", id);
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/{catId}")
    public CategoryResponseDto updateCategory(@PathVariable(value = "catId") @Min(1) Long id,
                                              @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        log.info("Обновление категории с идентификатором: {}", id);
        return categoryService.updateCategory(id, categoryRequestDto);
    }

}