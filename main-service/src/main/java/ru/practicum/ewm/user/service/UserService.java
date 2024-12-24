package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserCreateRequestDto;
import ru.practicum.ewm.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUser(List<Long> ids, Integer from, Integer size);

    UserResponseDto createUser(UserCreateRequestDto inDto);

    void deleteUser(Long userId);

}