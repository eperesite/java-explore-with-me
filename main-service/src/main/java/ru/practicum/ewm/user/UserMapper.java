package ru.practicum.ewm.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.user.dto.UserCreateRequestDto;
import ru.practicum.ewm.user.dto.UserResponseDto;
import ru.practicum.ewm.user.dto.UserShortResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .emailAddress(user.getEmailAddress())
                .build();
    }

    public static User toUser(UserCreateRequestDto userRequest) {
        return new User(null, userRequest.getFullName(), userRequest.getEmailAddress());
    }

    public static UserShortResponseDto toUserShortResponseDto(User user) {
        return UserShortResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .build();
    }
}