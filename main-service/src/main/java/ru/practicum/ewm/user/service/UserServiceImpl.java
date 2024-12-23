package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.UserCreateRequestDto;
import ru.practicum.ewm.user.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserResponseDto createUser(UserCreateRequestDto userRequest) {
        return UserMapper.toUserResponseDto(userRepository.save(UserMapper.toUser(userRequest)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponseDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<UserResponseDto> userResponses;

        if (userIds != null && !userIds.isEmpty()) {
            userResponses = userRepository.findByUserIdIn(userIds, page).stream()
                    .map(UserMapper::toUserResponseDto)
                    .collect(Collectors.toList());
        } else {
            userResponses = userRepository.findAll(page).stream()
                    .map(UserMapper::toUserResponseDto)
                    .collect(Collectors.toList());
        }
        return userResponses;
    }
}