package ru.practicum.ewm.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static CategoryResponseDto toCategoryOutDto(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("не может иметь значения null");
        }
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRequestDto == null || categoryRequestDto.getName() == null || categoryRequestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("CategoryRequestDto  и ее имя не могут быть null или пустыми.");
        }
        return new Category(null, categoryRequestDto.getName().trim());
    }
}
