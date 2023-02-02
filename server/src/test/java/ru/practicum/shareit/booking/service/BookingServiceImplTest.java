package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.item.Status;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingService bookingService;

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldCreate() {
        BookingDtoInput inputBookingDto = new BookingDtoInput(
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2),
                5L
        );

        BookingDtoOutput newBookingDto = bookingService.create(1L, inputBookingDto);
        assertEquals(newBookingDto.getId(), 1L);
        assertEquals(newBookingDto.getStatus(), Status.WAITING);
        assertEquals(newBookingDto.getId(), 1L);
        assertEquals(newBookingDto.getItem().getId(), 5L);
        assertEquals(newBookingDto.getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-without-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfItemIsUnavailable() {
        BookingDtoInput newBookingDto = new BookingDtoInput(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );

        assertThrows(
                AccessException.class,
                () -> bookingService.create(1L, newBookingDto),
                "This item is not available."
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfUserNotExist() {
        long userId = 99L;
        BookingDtoInput newBookingDto = new BookingDtoInput(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );

        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(userId, newBookingDto),
                "This user is not exist.");
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfItemNotExist() {
        BookingDtoInput newBookingDto = new BookingDtoInput(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                99L
        );

        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(1L, newBookingDto),
                "This item is not exist."
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfBookerIsEqualsOwner() {
        BookingDtoInput newBookingDto = new BookingDtoInput(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );

        assertThrows(
                AccessException.class,
                () -> bookingService.create(1L, newBookingDto),
                "Owner cannot book his item"
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfTimeCollapse() {
        BookingDtoInput newBookingDto = new BookingDtoInput(
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now(),
                3L
        );

        assertThrows(
                TimeException.class,
                () -> bookingService.create(1L, newBookingDto),
                "You are not in Nolan movie :)"
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldReturnById() {
        BookingDtoOutput actual = bookingService.getById(1L, 7L);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.getId());
        assertEquals(LocalDateTime.of(2023, 1, 20, 12, 0), actual.getStart());
        assertEquals(LocalDateTime.of(2023, 2, 15, 12, 0), actual.getEnd());
        assertEquals(3L, actual.getItem().getId());
        assertEquals(1L, actual.getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfWrongUserId() {
        long id = 99L;

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(id, 7L));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldReturnById_IfUserIsBooker() {
        BookingDtoOutput actual = bookingService.getById(1L, 7L);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.getId());
        assertEquals(LocalDateTime.of(2023, 1, 20, 12, 0), actual.getStart());
        assertEquals(LocalDateTime.of(2023, 2, 15, 12, 0), actual.getEnd());
        assertEquals(3L, actual.getItem().getId());
        assertEquals(1L, actual.getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-without-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfWrongId() {
        long bookingId = 99L;

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1L, bookingId),
                "Booking with this id not exist."
        );
    }


    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getByUser_ShouldThrowExceptionIfWrongUserId() {
        long id = 99L;

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByUser(id, State.ALL, 0, 10)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByUserIfStateIsWaiting() {
        List<BookingDtoOutput> actual = bookingService.getAllByUser(1L, State.WAITING, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-rejected-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByUserIfStateIsRejected() {
        List<BookingDtoOutput> actual = bookingService.getAllByUser(1L, State.REJECTED, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.REJECTED);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-unsupported-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldThrowExceptionIfStateIsUnsupported() {

        assertThrows(
                UnknownStateException.class,
                () -> bookingService.getAllByUser(1L, State.UNSUPPORTED_STATUS, 0, 10)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-current-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByUserIfStateIsCurrent() {
        List<BookingDtoOutput> actual = bookingService.getAllByUser(1L, State.CURRENT, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-past-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByUserIfStateIsPast() {
        List<BookingDtoOutput> actual = bookingService.getAllByUser(1L, State.PAST, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-future-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByUserIfStateIsFuture() {
        List<BookingDtoOutput> actual = bookingService.getAllByUser(1L,State.FUTURE, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isAfter(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByUserIfStateIsAll() {
        List<BookingDtoOutput> actual = bookingService.getAllByUser(1L, State.ALL, 0, 1);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.get(0).getId());
        assertEquals(LocalDateTime.of(2023, 1, 20, 12, 0), actual.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 2, 15, 12, 0), actual.get(0).getEnd());
        assertEquals(3L, actual.get(0).getItem().getId());
        assertEquals(1L, actual.get(0).getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByOwnerIfStateIsWaiting() {
        List<BookingDtoOutput> actual = bookingService.getAllByOwner(1L, State.WAITING, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-rejected-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByOwnerIfStateIsRejected() {
        List<BookingDtoOutput> actual = bookingService.getAllByOwner(1L, State.REJECTED, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.REJECTED);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-current-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByOwnerIfStateIsCurrent() {
        List<BookingDtoOutput> actual = bookingService.getAllByOwner(1L, State.CURRENT, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-past-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByOwnerIfStateIsPast() {
        List<BookingDtoOutput> actual = bookingService.getAllByOwner(1L, State.PAST, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-future-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByOwnerIfStateIsFuture() {
        List<BookingDtoOutput> actual = bookingService.getAllByOwner(1L, State.FUTURE, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), Status.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isAfter(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldByOwnerIfStateIsAll() {
        List<BookingDtoOutput> actual = bookingService.getAllByOwner(1L, State.ALL, 0, 1);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.get(0).getId());
        assertEquals(LocalDateTime.of(2023, 1, 20, 12, 0), actual.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 2, 15, 12, 0), actual.get(0).getEnd());
        assertEquals(3L, actual.get(0).getItem().getId());
        assertEquals(1L, actual.get(0).getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void shouldUpdateStatus() {
        BookingDtoOutput actual = bookingService.updateStatus(1L, 7L, true);

        assertEquals(actual.getStatus(), Status.APPROVED);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateStatus_ShouldThrowExceptionIfWrongId() {
        long bookingId = 99L;

        assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(1L, bookingId, true)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateStatus_ShouldThrowExceptionIfUserIsNotOwner() {
        long id = 99L;

        assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(id, 7L, true)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before-with-approved-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateStatus_ShouldThrowExceptionIfStatusEqualActual() {
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateStatus(1L, 7L, true)

        );
    }
}