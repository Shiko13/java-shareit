package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoInputTest {
    @Autowired
    private JacksonTester<BookingDtoInput> jacksonTester;

    @Test
    void serialization() throws IOException {
        User user = new User(1L, "Oleg", "oleg@yandex.ru");
        Item item = new Item(2L, "Dryer", "For curly hair", true, user, null);
        BookingDtoInput bookingDtoInput = new BookingDtoInput(LocalDateTime.now().plusHours(1).withNano(0),
                LocalDateTime.now().plusHours(2).withNano(0), item.getId());
        JsonContent<BookingDtoInput> result = jacksonTester.write(bookingDtoInput);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(bookingDtoInput.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(bookingDtoInput.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId");
    }
}