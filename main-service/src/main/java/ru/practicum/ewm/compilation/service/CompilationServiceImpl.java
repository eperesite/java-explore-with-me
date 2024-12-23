package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequestDto;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflictException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationManagementService, CompilationQueryService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationResponseDto createCompilation(CompilationRequestDto request) {
        Compilation compilation = CompilationMapper.toEntity(request);
        compilation.setPinned(Optional.ofNullable(request.getPinned()).orElse(false));

        Set<Long> eventIds = Optional.ofNullable(request.getEventIds()).orElse(Collections.emptySet());
        List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
        compilation.setEvents(new HashSet<>(events));

        return CompilationMapper.toResponseDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public CompilationResponseDto updateCompilation(Long compilationId, CompilationUpdateRequestDto update) {
        Compilation compilation = findCompilationById(compilationId);

        if (update.getEventIds() != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(update.getEventIds()));
            compilation.setEvents(new HashSet<>(events));
        }

        compilation.setPinned(Optional.ofNullable(update.getPinned()).orElse(compilation.getPinned()));
        if (update.getTitle() != null && update.getTitle().isBlank()) {
            throw new ValidationConflictException("Title cannot be blank");
        }
        compilation.setTitle(Optional.ofNullable(update.getTitle()).orElse(compilation.getTitle()));

        return CompilationMapper.toResponseDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Compilation with ID " + compilationId + " not found");
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public List<CompilationResponseDto> getAllCompilations(Boolean pinned, Integer offset, Integer limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        List<Compilation> compilations = pinned == null
                ? compilationRepository.findAll(pageRequest).getContent()
                : compilationRepository.findAllByPinned(pinned, pageRequest);

        return compilations.stream()
                .map(CompilationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto getCompilationById(Long compilationId) {
        return CompilationMapper.toResponseDto(findCompilationById(compilationId));
    }

    private Compilation findCompilationById(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with ID " + compilationId + " not found"));
    }
}