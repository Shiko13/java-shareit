package ru.practicum.shareit.user.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

class UserDtoConverterTest {

    @Test
    void toDto() {
        User user = new User(1L, "Oleg", "oleg@yandex.ru");
        UserDto userDto = UserDtoConverter.toDto(user);

        Assertions.assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Oleg")
                .hasFieldOrPropertyWithValue("email", "oleg@yandex.ru");
    }

    @Test
    void fromDto() {
        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        User user = UserDtoConverter.fromDto(userDto);

        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Oleg")
                .hasFieldOrPropertyWithValue("email", "oleg@yandex.ru");
    }
}