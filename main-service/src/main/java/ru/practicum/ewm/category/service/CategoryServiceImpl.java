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
public class CategoryServiceImpl implements CategoryManagementService, CategoryQueryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return CategoryMapper.toResponseDto(findCategoryEntity(id));
    }

    @Override
    public List<CategoryResponseDto> getAllCategories(Integer offset, Integer limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        return CategoryMapper.toResponseDto(categoryRepository.save(CategoryMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category with ID " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        Category category = findCategoryEntity(id);

        if (request.getName() != null && !category.getName().equals(request.getName()) && isNameUnique(request.getName())) {
            category.setName(request.getName());
            category = categoryRepository.save(category);
        }

        return CategoryMapper.toResponseDto(category);
    }

    private Category findCategoryEntity(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Category with ID " + id + " does not exist"));
    }

    private boolean isNameUnique(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidationConflictException("Category with name " + name + " already exists");
        }
        return true;
    }
}