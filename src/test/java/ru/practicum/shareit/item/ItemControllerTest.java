package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private final UserDto userDtoOleg = new UserDto(1L, "Oleg", "oleg@yandex.ru");
    private final UserDtoShort userDtoShortOleg = new UserDtoShort(userDtoOleg.getId(), userDtoOleg.getName());
    private final UserDto userDtoIrina = new UserDto(2L, "Irina", "irina@yandex.ru");
    private final ItemDto itemDtoDryer = new ItemDto(3L, "Dryer", "For curly hair",
            true, userDtoIrina, 1L);
    private final ItemDtoShort itemDtoShortDryer = new ItemDtoShort(itemDtoDryer.getId(), itemDtoDryer.getName());
    private final ItemDtoInput itemDtoInputDryer = new ItemDtoInput(itemDtoDryer.getId(), itemDtoDryer.getName(),
            itemDtoDryer.getDescription(), itemDtoDryer.getAvailable(), itemDtoDryer.getRequestId());
    private final CommentDto comment = new CommentDto(4L, "Hot!", userDtoOleg.getName(),
            LocalDateTime.of(2023, 1, 20, 12, 10)
    );
    private final BookingDtoOutput lastBooking = new BookingDtoOutput(
            5L,
            LocalDateTime.of(2023, 1, 20, 12, 0),
            LocalDateTime.of(2023, 2, 15, 12, 0),
            itemDtoShortDryer, userDtoShortOleg, Status.APPROVED
    );

    private final BookingDtoOnlyIdAndBookerId lastBookingShort = new BookingDtoOnlyIdAndBookerId(
            lastBooking.getId(), lastBooking.getBooker().getId());

    private final BookingDtoOutput nextBooking = new BookingDtoOutput(
            6L,
            LocalDateTime.of(2023, 2, 10, 12, 0),
            LocalDateTime.of(2023, 2, 15, 12, 0),
            itemDtoShortDryer, userDtoShortOleg, Status.APPROVED
    );
    private final BookingDtoOnlyIdAndBookerId nextBookingShort = new BookingDtoOnlyIdAndBookerId(
            nextBooking.getId(), nextBooking.getBooker().getId());
    private final ItemDtoWithBookingAndComments itemWithCommentsAndBookings = new ItemDtoWithBookingAndComments(
            itemDtoDryer.getId(),
            itemDtoDryer.getName(),
            itemDtoDryer.getDescription(),
            itemDtoDryer.getAvailable(),
            lastBookingShort, nextBookingShort, List.of(comment));
    private final String commentCreated = "2023-01-20T12:10:00";

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create_shouldSuccess() throws Exception {
        Mockito
                .when(itemService.create(anyLong(), any()))
                .thenReturn(itemDtoDryer);

        mvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", userDtoOleg.getId())
                                .content(mapper.writeValueAsString(itemDtoDryer))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoDryer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoDryer.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoDryer.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoDryer.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDtoDryer.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(itemDtoDryer.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDtoDryer.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(itemDtoDryer.getRequestId()), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .create(1L, itemDtoInputDryer);
    }

    @Test
    void createComment_shouldSuccess() throws Exception {
        Mockito.when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(
                        post("/items/{itemId}/comment", itemDtoDryer.getId())
                                .header("X-Sharer-User-Id", userDtoOleg.getId())
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentCreated)));

        Mockito.verify(itemService, Mockito.times(1))
                .createComment(1L, 3L, comment);
    }

    @Test
    void getById_shouldSuccess() throws Exception {
        Mockito
                .when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemWithCommentsAndBookings);

        mvc.perform(
                        get("/items/{itemId}", itemDtoDryer.getId())
                                .header("X-Sharer-User-Id", userDtoIrina.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithCommentsAndBookings.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithCommentsAndBookings.getName())))
                .andExpect(jsonPath("$.description", is(itemWithCommentsAndBookings.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithCommentsAndBookings.getAvailable())))
                .andExpect(
                        jsonPath("$.comments[0].id", is(itemWithCommentsAndBookings
                                .getComments().get(0).getId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.comments[0].text", is(itemWithCommentsAndBookings
                                .getComments().get(0).getText()))
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].authorName",
                                is(itemWithCommentsAndBookings.getComments().get(0).getAuthorName())
                        )
                )
                .andExpect(jsonPath("$.comments[0].created", is(commentCreated)))


                .andExpect(
                        jsonPath("$.lastBooking.id", is(itemWithCommentsAndBookings
                                .getLastBooking().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.lastBooking.bookerId",
                                is(itemWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.nextBooking.id", is(itemWithCommentsAndBookings
                                .getNextBooking().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.nextBooking.bookerId",
                                is(itemWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                        )
                );

        Mockito.verify(itemService, Mockito.times(1))
                .getById(2L, 3L);
    }

    @Test
    void getAll_shouldSuccess() throws Exception {
        Mockito
                .when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemWithCommentsAndBookings));

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", userDtoIrina.getId())
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemWithCommentsAndBookings.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemWithCommentsAndBookings.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemWithCommentsAndBookings
                        .getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemWithCommentsAndBookings.getAvailable())))
                .andExpect(
                        jsonPath("$.[0].comments[0].id", is(itemWithCommentsAndBookings
                                        .getComments().get(0).getId()),
                                Long.class)
                )
                .andExpect(
                        jsonPath("$.[0].comments[0].text", is(itemWithCommentsAndBookings
                                .getComments().get(0).getText()))
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].authorName",
                                is(itemWithCommentsAndBookings.getComments().get(0).getAuthorName())
                        )
                )
                .andExpect(jsonPath("$.[0].comments[0].created", is(commentCreated)))
                .andExpect(
                        jsonPath("$.[0].lastBooking.id", is(itemWithCommentsAndBookings
                                .getLastBooking().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.[0].lastBooking.bookerId",
                                is(itemWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.[0].nextBooking.id", is(itemWithCommentsAndBookings.getNextBooking().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.[0].nextBooking.bookerId",
                                is(itemWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                        )
                );

        Mockito.verify(itemService, Mockito.times(1))
                .getAll(2L, 0, 1);
    }

    @Test
    void update_shouldSuccess() throws Exception {
        Mockito
                .when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDtoDryer);

        mvc.perform(
                        patch("/items/{itemId}", itemDtoDryer.getId())
                                .header("X-Sharer-User-Id", userDtoIrina.getId())
                                .content(mapper.writeValueAsString(itemDtoDryer))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoDryer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoDryer.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoDryer.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoDryer.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDtoDryer.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(itemDtoDryer.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDtoDryer.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(itemDtoDryer.getRequestId()), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .update(2L, 3L, itemDtoDryer);
    }

    @Test
    void deleteById_shouldSuccess() throws Exception {
        itemService.deleteById(anyLong(), anyLong());

        Mockito
                .verify(itemService, Mockito.times(1))
                .deleteById(anyLong(), anyLong());

        mvc.perform(
                        delete("/items/{itemId}", itemDtoDryer.getId())
                                .header("X-Sharer-User-Id", userDtoIrina.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getByText_shouldSuccess() throws Exception {
        Mockito
                .when(itemService.getByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoDryer));

        mvc.perform(
                        get("/items/search")
                                .param("text", "table")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDtoDryer.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoDryer.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoDryer.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoDryer.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(itemDtoDryer.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(itemDtoDryer.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(itemDtoDryer.getOwner().getEmail())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoDryer.getRequestId()), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .getByText("table", 0, 1);
    }
}