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
import ru.practicum.ewm.exception.ValidatetionConflict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        CategoryResponseDto categoryResponseDto;
        categoryResponseDto = CategoryMapper.toCategoryOutDto(findCategory(id));
        return categoryResponseDto;
    }

    @Override
    public List<CategoryResponseDto> getCategory(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(CategoryMapper::toCategoryOutDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        return CategoryMapper.toCategoryOutDto(categoryRepository.save(CategoryMapper.toCategory(categoryRequestDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с id= " + id + " не найден");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с id= " + id + " не найдена");
        }
        Category category = findCategory(id);

        if (categoryRequestDto.getName() != null && !category.getName().equals(categoryRequestDto.getName()) && checkName(categoryRequestDto.getName())) {

            category.setName(categoryRequestDto.getName());
            category = categoryRepository.save(category);

        }
        return CategoryMapper.toCategoryOutDto(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категории с id = " + id + " не существует"));
    }

    private boolean checkName(String name) {

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidatetionConflict("Категория с названием " + name + " уже существует");
        }
        return true;
    }
}