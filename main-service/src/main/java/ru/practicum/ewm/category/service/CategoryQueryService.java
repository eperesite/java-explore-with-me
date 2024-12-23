package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryQueryService {
    CategoryResponseDto getCategoryById(Long id);

    List<CategoryResponseDto> getAllCategories(Integer offset, Integer limit);
}