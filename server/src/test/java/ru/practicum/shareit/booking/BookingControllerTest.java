package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final UserDtoShort userOlegDto = new UserDtoShort(userOleg.getId(), userOleg.getName());
    private final User userIrina = new User(2L, "Irina", "irina@yandex.ru");
    private final Item dryer = new Item(3L, "Dryer", "For curly hair", false,
            userIrina, null);
    private final ItemDtoShort dryerDto = new ItemDtoShort(dryer.getId(), dryer.getName());
    private final BookingDtoOutput bookingDto = new BookingDtoOutput(
            4L,
            LocalDateTime.of(2023, 5, 20, 12, 0),
            LocalDateTime.of(2023, 5, 24, 12, 0),
            dryerDto, userOlegDto, Status.WAITING);
    private final BookingDtoOutput approved = new BookingDtoOutput(bookingDto.getId(), bookingDto.getStart(),            bookingDto.getEnd(),
            bookingDto.getItem(), bookingDto.getBooker(), Status.APPROVED);
    private final BookingDtoInput inputBookingDto = new BookingDtoInput(bookingDto.getStart(), bookingDto.getEnd(),
            bookingDto.getBooker().getId());
    private final String startDate = "2023-05-20T12:00:00";
    private final String endDate = "2023-05-24T12:00:00";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldCreate() throws Exception {
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", userOleg.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        Mockito
                .verify(bookingService, Mockito.times(1))
                .create(1L, inputBookingDto);
    }

    @Test
    void shouldReturnNotFoundIfUserWrong() throws Exception {
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenThrow(new NotFoundException("User with this id not found"));

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", userOleg.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestIfItemUnavailable() throws Exception {
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenThrow(new AvailabilityException("Item is not available"));

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", userOleg.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForbiddenCreate() throws Exception {
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenThrow(new NotFoundException("User with this id cannot add item with this id"));

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", userIrina.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldUpdateStatus() throws Exception {
        Mockito
                .when(bookingService.updateStatus(anyLong(), anyLong(), eq(true)))
                .thenReturn(approved);

        mvc.perform(
                        patch("/bookings/{bookingId}", bookingDto.getId())
                                .header("X-Sharer-User-Id", userIrina.getId())
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(approved.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(approved.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(approved.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(approved.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(approved.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(approved.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .updateStatus(2L, 4L, true);
    }

    @Test
    void updateStatus_shouldThrowException() throws Exception {
        Mockito
                .when(bookingService.updateStatus(anyLong(), anyLong(), eq(true)))
                .thenThrow(new AccessException("You are not owner of this item"));

        mvc.perform(
                        patch("/bookings/{bookingId}", bookingDto.getId())
                                .header("X-Sharer-User-Id", userIrina.getId())
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1))
                .updateStatus(2L, 4L, true);
    }

    @Test
    void shouldReturnById() throws Exception {
        Mockito
                .when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(
                        get("/bookings/{bookingId}", bookingDto.getId())
                                .header("X-Sharer-User-Id", userOleg.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getById(1L, 4L);
    }

    @Test
    void shouldReturnByUser() throws Exception {
        Mockito
                .when(bookingService.getAllByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", userOleg.getId())
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(startDate)))
                .andExpect(jsonPath("$.[0].end", is(endDate)))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllByUser(1L, State.ALL, 0, 1);
    }

    @Test
    void shouldReturnByOwner() throws Exception {
        Mockito
                .when(bookingService.getAllByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", userOleg.getId())
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(startDate)))
                .andExpect(jsonPath("$.[0].end", is(endDate)))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));
    }
}