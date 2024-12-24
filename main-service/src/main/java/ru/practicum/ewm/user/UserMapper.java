package ru.practicum.ewm.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.user.dto.UserCreateRequestDto;
import ru.practicum.ewm.user.dto.UserResponseDto;
import ru.practicum.ewm.user.dto.UserShortResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserResponseDto toUserOutDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateRequestDto userCreateRequestDto) {
        User user = new User(null,
                userCreateRequestDto.getName(),
                userCreateRequestDto.getEmail());
        return user;
    }

    public static UserShortResponseDto toUserShortDto(User user) {
        return UserShortResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}