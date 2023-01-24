package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final UserDto userDtoOleg = new UserDto(userOleg.getId(), userOleg.getName(), userOleg.getEmail());
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_shouldBeSuccess() {
        Mockito
                .when(mockUserRepository.save(any()))
                .thenReturn(userOleg);
        UserDto user = userService.create(userDtoOleg);

        assertNotNull(user);
        assertEquals(userDtoOleg, user);
    }

    @Test
    void getById_shouldBeSuccess() {
        Mockito
                .when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(userOleg));
        UserDto user = userService.getById(userOleg.getId());

        assertEquals(userDtoOleg, user);
    }

    @Test
    void getById_shouldThrowExceptionIfWrongUserId() {
        Mockito
                .when(mockUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void getAll_shouldBeSuccess() {
        Mockito
                .when(mockUserRepository.findAll()).thenReturn(List.of(userOleg));
        List<UserDto> users = userService.getAll();

        assertEquals(List.of(userDtoOleg), users);
    }

    @Test
    void update_shouldBeSuccess() {
        User userIrina = new User(2L, "Irina", "irina@yandex.ru");
        UserDto userIrinaUpdate = new UserDto(userIrina.getId(), "Irina", "irinajunior@yandex.ru");
        User updatedIrina = new User(userIrina.getId(), userIrinaUpdate.getName(), userIrinaUpdate.getEmail());
        UserDto userIrinaDto = new UserDto(userIrina.getId(), userIrinaUpdate.getName(), userIrinaUpdate.getEmail());
        Mockito
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        lenient()
                .when(mockUserRepository.save(userIrina))
                .thenReturn(updatedIrina);
        UserDto actualIrina = userService.update(userIrina.getId(), userIrinaUpdate);

        assertEquals(userIrinaDto, actualIrina);
    }

    @Test
    void update_shouldBeSuccessWithNulls() {
        User userIrina = new User(2L, "Irina", "irina@yanderx.ru");
        UserDto userDtoIrina = new UserDto(userIrina.getId(), userIrina.getName(), userIrina.getEmail());
        UserDto updatedIrina = new UserDto(userIrina.getId(), null, null);
        Mockito
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        lenient()
                .when(mockUserRepository.save(userIrina))
                .thenReturn(userIrina);
        UserDto user = userService.update(userIrina.getId(), updatedIrina);

        assertEquals(userDtoIrina, user);
    }

    @Test
    void update_shouldThrowExceptionIfWrongUserId() {
        UserDto userDtoIrina = new UserDto(null, "Irina", "irinajunior@yanderx.ru");
        Mockito
                .when(mockUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(99L, userDtoIrina));
    }

    @Test
    void deleteById_shouldBeSuccess() {
        User userOleg = new User(2L, "Oleg", "oleg@yandex.ru");
        userService.deleteById(userOleg.getId());
        Mockito
                .verify(mockUserRepository, Mockito.times(1)).deleteById(userOleg.getId());
    }
}