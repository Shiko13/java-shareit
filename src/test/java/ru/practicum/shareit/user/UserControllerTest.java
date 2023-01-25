package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private final UserDto userJohn = new UserDto(1L, "John", "john@yandex.ru");
    private final UserDto updateUserJohn = new UserDto(null, "John", "johnjunior@yandex.ru");
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create_shouldBeSuccess() throws Exception {
        Mockito
                .when(userService.create(any()))
                .thenReturn(userJohn);

        mvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userJohn))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userJohn.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userJohn.getName())))
                .andExpect(jsonPath("$.email", is(userJohn.getEmail())));


        Mockito.verify(userService, times(1))
                .create(userJohn);
    }

    @Test
    void getById_shouldBeSuccess() throws Exception {
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(userJohn);

        mvc.perform(
                        get("/users/{id}", userJohn.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userJohn.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userJohn.getName())))
                .andExpect(jsonPath("$.email", is(userJohn.getEmail())));

        Mockito.verify(userService, times(1))
                .getById(1L);
    }

    @Test
    void getAll_shouldBeSuccess() throws Exception {
        Mockito
                .when(userService.getAll())
                .thenReturn(List.of(userJohn));

        mvc.perform(
                        get("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userJohn.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userJohn.getName())))
                .andExpect(jsonPath("$.[0].email", is(userJohn.getEmail())));

        Mockito.verify(userService, times(1))
                .getAll();
    }

    @Test
    void update_shouldBeSuccess() throws Exception {
        Mockito
                .when(userService.update(anyLong(), any()))
                .thenReturn(userJohn);

        mvc.perform(
                        patch("/users/{id}", userJohn.getId())
                                .content(mapper.writeValueAsString(updateUserJohn))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userJohn.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userJohn.getName())))
                .andExpect(jsonPath("$.email", is(userJohn.getEmail())));

        Mockito.verify(userService, times(1))
                .update(1L, updateUserJohn);
    }

    @Test
    void update_shouldThrowExceptionIfEmailIsBlank() throws Exception {
        UserDto user = new UserDto(1L, "Oleg", "");
        UserDto updatedUser = new UserDto(1L, "Oleg", "");

        Mockito
                .when(userService.update(anyLong(), any()))
                .thenThrow(new ValidateException(""));

        mvc.perform(
                        patch("/users/{id}", user.getId())
                                .content(mapper.writeValueAsString(updatedUser))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, times(1))
                .update(1L, updatedUser);
    }

    @Test
    void deleteById_shouldBeSuccess() throws Exception {
        userService.deleteById(anyLong());
        Mockito
                .verify(userService, times(1))
                .deleteById(anyLong());

        mvc.perform(
                        delete("/users/{id}", userJohn.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteAll_shouldSuccess() throws Exception {
        userService.deleteAll();

        Mockito
                .verify(userService, Mockito.times(1))
                .deleteAll();

        mvc.perform(
                        delete("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}