package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@SqlGroup({
        @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final User userIrina = new User(2L, "Irina", "irina@yandex.ru");
    private final ItemRequest request = new ItemRequest(
            4L,
            "I want to dry my hair",
            userIrina,
            LocalDateTime.of(2023, 1, 22, 12, 0));
    private final Item itemDryer = new Item(3L,
            "Dryer",
            "For curly hair",
            true,
            userOleg,
            request);
    private final Booking booking = new Booking(
            4L,
            LocalDateTime.of(2023, 12, 10, 12, 0),
            LocalDateTime.of(2023, 12, 21, 12, 0),
            itemDryer,
            userIrina,
            Status.APPROVED);
    private final Item itemHammer = new Item(5L, "Hammer",
            "With gold handle", true, userIrina, null);
    private final Booking bookingCurrent = new Booking(
            7L,
            LocalDateTime.of(2023, 1, 20, 12, 0),
            LocalDateTime.of(2023, 2, 15, 12, 0),
            itemHammer,
            userOleg,
            Status.APPROVED);
    private final Booking bookingPast = new Booking(
            6L,
            LocalDateTime.of(2013, 1, 1, 12, 0),
            LocalDateTime.of(2013, 2, 2, 12, 0),
            itemHammer,
            userOleg,
            Status.APPROVED);
    private final Set<Long> setIds = Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);

    @Test
    void shouldFindByItemId_AndStartBefore_OrderByEndDesc() {
        List<Booking> result = bookingRepository.findByItem_IdInAndStartBeforeOrderByEndDesc(setIds);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(bookingCurrent.getId());
        assertThat(result.get(1).getId()).isEqualTo(bookingPast.getId());
    }

    @Test
    void shouldFindByItemId_AndStartAfter_OrderByEndAsc() {
        List<Booking> result = bookingRepository.findByItem_IdInAndStartAfterOrderByEndAsc(setIds);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    }
}