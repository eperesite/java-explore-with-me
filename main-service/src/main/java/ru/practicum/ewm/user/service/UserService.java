package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserCreateRequestDto;
import ru.practicum.ewm.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserResponseDto createUser(UserCreateRequestDto userRequest);

    void deleteUser(Long userId);
}