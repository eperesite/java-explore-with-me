package ru.practicum.ewm.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.service.CategoryQueryService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryQueryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
                                            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        log.info("Fetching categories with offset: {} and limit: {}", offset, limit);
        return categoryService.getAllCategories(offset, limit);
    }

    @GetMapping("/{catId}")
    public CategoryResponseDto getById(@PathVariable("catId") Long id) {
        log.info("Fetching category with ID: {}", id);
        return categoryService.getCategoryById(id);
    }
}