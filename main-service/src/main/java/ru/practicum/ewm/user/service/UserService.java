package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserCreateRequestDto;
import ru.practicum.ewm.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> get(List<Long> ids, Integer from, Integer size);

    UserResponseDto create(UserCreateRequestDto inDto);

    void delete(Long userId);

}