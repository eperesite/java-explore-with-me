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
import ru.practicum.ewm.category.service.CategoryManagementService;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryManagementService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto create(@RequestBody @Valid CategoryRequestDto request) {
        log.info("Creating a new category: {}", request);
        return categoryService.createCategory(request);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("categoryId") Long id) {
        log.info("Deleting category with ID: {}", id);
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/{catId}")
    public CategoryResponseDto update(@PathVariable("categoryId") @Min(1) Long id,
                                      @RequestBody @Valid CategoryRequestDto request) {
        log.info("Updating category with ID: {}", id);
        return categoryService.updateCategory(id, request);
    }
}