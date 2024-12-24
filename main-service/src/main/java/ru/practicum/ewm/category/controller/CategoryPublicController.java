package ru.practicum.ewm.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение категорий с размерами");
        return categoryService.getCategory(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryResponseDto getCategory(@PathVariable(value = "catId") Long id) {
        log.info("Запрос на получение категории с id={}", id);
        return categoryService.getCategoryById(id);
    }
}