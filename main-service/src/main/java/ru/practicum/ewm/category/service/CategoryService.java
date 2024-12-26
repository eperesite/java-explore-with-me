package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto getCategoryById(Long id);

    List<CategoryResponseDto> getCategory(Integer from, Integer size);

    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    void deleteCategory(Long id);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto);

}