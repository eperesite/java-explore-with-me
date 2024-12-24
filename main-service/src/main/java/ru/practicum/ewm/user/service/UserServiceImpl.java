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
public class UserServiceImpl implements UserService  {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserResponseDto createUser(UserCreateRequestDto userCreateRequestDto) {
        return UserMapper.toUserOutDto(userRepository.save(UserMapper.toUser(userCreateRequestDto)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponseDto> getUser(List<Long> listId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<UserResponseDto> listUserResponseDto;

        if (listId != null && !listId.isEmpty()) {
            listUserResponseDto = userRepository.findByIdIn(listId, page).stream()
                    .map(UserMapper::toUserOutDto)
                    .collect(Collectors.toList());
        } else {
            listUserResponseDto = userRepository.findAll(page).stream()
                    .map(UserMapper::toUserOutDto)
                    .collect(Collectors.toList());
        }

        return listUserResponseDto;
    }
}