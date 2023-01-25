package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        UserDto user = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        JsonContent<UserDto> result = jacksonTester.write(user);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(user.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathValue("$.email").isEqualTo(user.getEmail());
    }
}