package ru.practicum.yandex.main.users;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.main.users.dto.NewUserRequest;
import ru.practicum.yandex.main.users.dto.UserDto;
import ru.practicum.yandex.main.users.dto.UserShortDto;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
