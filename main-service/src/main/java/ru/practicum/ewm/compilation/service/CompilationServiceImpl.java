package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflictException;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequestDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationResponseDto createCompilation(CompilationRequestDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));

        Set<Long> eventIds = Optional.ofNullable(compilationDto.getEvents()).orElse(Collections.emptySet());
        List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));

        if (events.size() != eventIds.size()) {
            throw new ValidationConflictException("Некоторые события не найдены в базе данных");
        }

        compilation.setEvents(new HashSet<>(events));

        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    @Transactional
    @Override
    public CompilationResponseDto updateCompilation(Long compId, CompilationUpdateRequestDto update) {
        Compilation compilation = checkCompilation(compId);

        if (update.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(update.getEvents()));
            if (events.size() != update.getEvents().size()) {
                throw new ValidationConflictException("Некоторые события не найдены в базе данных");
            }
            compilation.setEvents(new HashSet<>(events));
        }

        compilation.setPinned(Optional.ofNullable(update.getPinned()).orElse(compilation.getPinned()));

        if (update.getTitle() != null) {
            String newTitle = update.getTitle().trim();
            if (newTitle.isEmpty()) {
                throw new ValidationConflictException("Название подборки не может быть пустым или состоять из пробелов");
            }
            compilation.setTitle(newTitle);
        }

        return CompilationMapper.toDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = checkCompilation(compId);
        compilationRepository.delete(compilation);
    }

    @Override
    public List<CompilationResponseDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Параметры пагинации должны быть положительными");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto findCompilationById(Long compId) {
        return CompilationMapper.toDto(checkCompilation(compId));
    }

    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с идентификатором " + compId + " не найдена"));
    }
}
