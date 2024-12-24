package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflictException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return CategoryMapper.toCategoryOutDto(findCategoryById(id));
    }

    @Override
    public List<CategoryResponseDto> getCategory(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("'from' должно быть >= 0, а 'size' - > 0.");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(CategoryMapper::toCategoryOutDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        validateCategoryName(categoryRequestDto.getName());
        Category category = CategoryMapper.toCategory(categoryRequestDto);
        return CategoryMapper.toCategoryOutDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с id = " + id + " не найдена.");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = findCategoryById(id);

        if (categoryRequestDto.getName() != null && !category.getName().equalsIgnoreCase(categoryRequestDto.getName())) {
            validateCategoryName(categoryRequestDto.getName());
            category.setName(categoryRequestDto.getName());
        }

        return CategoryMapper.toCategoryOutDto(categoryRepository.save(category));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категория с id = " + id + " не существует."));
    }

    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationConflictException("Название категории не может быть пустым.");
        }

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidationConflictException("Категория с названием '" + name + "' уже существует.");
        }
    }
}
