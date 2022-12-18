package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public interface UserDtoConverter {
    UserDto toDto(User user);

    User fromDto(UserDto userDto);
}
