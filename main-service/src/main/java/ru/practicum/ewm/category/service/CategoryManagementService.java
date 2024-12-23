package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;

public interface CategoryManagementService {
    CategoryResponseDto createCategory(CategoryRequestDto request);

    void deleteCategory(Long id);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto request);
}