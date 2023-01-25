package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Status;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private final Set<Long> itemIds = new HashSet<>(Arrays.asList(6L, 7L));
    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final UserDto userDtoOleg = new UserDto(userOleg.getId(), userOleg.getName(), userOleg.getEmail());
    private final User userIrina = new User(2L, "Irina", "irina@yandex.ru");
    private final UserDto userDtoIrina = new UserDto(userIrina.getId(), userIrina.getName(), userIrina.getEmail());
    private final ItemRequest request = new ItemRequest(4L, "I want this dryer!", userIrina,
            LocalDateTime.of(2023, 1, 20, 12, 0, 0));
    private final Item dryer = new Item(3L, "Dryer", "For curly hair",
            true, userIrina, request);
    private final ItemDto dryerDto = new ItemDto(dryer.getId(), dryer.getName(),
            dryer.getDescription(), dryer.getAvailable(), userDtoIrina,
            dryer.getRequest().getId() == null ? null : dryer.getRequest().getId());
    private final ItemDtoInput dryerDtoInput = new ItemDtoInput(dryer.getId(), dryer.getName(),
            dryer.getDescription(), dryer.getAvailable(), dryer.getRequest().getId());
    private final Booking lastBooking = new Booking(6L,
            LocalDateTime.of(2023, 1, 10, 12, 0),
            LocalDateTime.of(2023, 1, 11, 12, 0),
            dryer, userOleg, Status.APPROVED);
    private final BookingDtoOnlyIdAndBookerId lastBookingShort = new BookingDtoOnlyIdAndBookerId(
            lastBooking.getId(), lastBooking.getBooker().getId());
    private final Booking nextBooking = new Booking(7L,
            LocalDateTime.of(2023, 2, 10, 12, 0),
            LocalDateTime.of(2023, 2, 15, 12, 0),
            dryer, userOleg, Status.APPROVED);
    private final BookingDtoOnlyIdAndBookerId nextBookingShort = new BookingDtoOnlyIdAndBookerId(
            nextBooking.getId(), nextBooking.getBooker().getId());
    private final ItemDtoWithBookingAndComments dryerDtoWithBookingsAndComments = new ItemDtoWithBookingAndComments(
            dryer.getId(), dryer.getName(), dryer.getDescription(),
            dryer.getAvailable(), lastBookingShort, nextBookingShort, new ArrayList<>()
    );
    private final Comment comment = new Comment(8L, "Amazing!", dryer, userOleg,
            LocalDateTime.of(2023, 1, 24, 12, 10));
    private final CommentDto commentDto = new CommentDto(comment.getId(),
            comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_shouldSuccess() {
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockItemRequestRepository.findById(dryerDto.getRequestId()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenReturn(dryer);
        ItemDto actual = itemService.create(userOleg.getId(), dryerDtoInput);

        assertEquals(dryerDto, actual);
    }

    @Test
    void create_shouldThrowExceptionIfUserNotExist() {
        Mockito
                .when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.create(99L, dryerDtoInput));
    }

    @Test
    void create_shouldThrowExceptionIfRequestNotExist() {
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockItemRequestRepository.findById(dryerDto.getRequestId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.create(userOleg.getId(), dryerDtoInput));
    }


    @Test
    void getById_shouldSuccess() {
        Mockito
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        Mockito
                .when(mockBookingRepository.findFirstByItem_IdAndStartIsLessThanEqualOrderByEndDesc(
                        eq(dryer.getId()),
                        any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(mockBookingRepository.findFirstByItem_IdAndStartAfterOrderByEndDesc(
                        eq(dryer.getId()),
                        any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));

        ItemDtoWithBookingAndComments actual = itemService.getById(
                userIrina.getId(), dryer.getId());

        assertEquals(dryerDtoWithBookingsAndComments, actual);
    }

    @Test
    void getById_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getById(userIrina.getId(), 99L)
        );
    }

    @Test
    void getById_shouldThrowExceptionIfUserNotExist() {
        lenient()
                .when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getById(99L, userIrina.getId())
        );
    }

    @Test
    void getById_shouldReturnWithBookingsAndCommentsIfUserIsOwner() {
        ItemDtoWithBookingAndComments expected = new ItemDtoWithBookingAndComments(
                dryer.getId(),
                dryer.getName(),
                dryer.getDescription(),
                dryer.getAvailable(),
                lastBookingShort, nextBookingShort, new ArrayList<>()
        );
        lenient()
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        Mockito
                .when(mockItemRepository.findById(dryer.getId())).thenReturn(Optional.of(dryer));
        Mockito
                .when(mockBookingRepository.findFirstByItem_IdAndStartIsLessThanEqualOrderByEndDesc(
                        eq(dryer.getId()),
                        any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(mockBookingRepository.findFirstByItem_IdAndStartAfterOrderByEndDesc(
                        eq(dryer.getId()),
                        any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));
        Mockito
                .when(mockCommentRepository.findByItem_Id(dryer.getId()))
                .thenReturn(Collections.emptyList());

        ItemDtoWithBookingAndComments actual = itemService.getById(
                userIrina.getId(), dryer.getId()
        );

        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnWithBookingsAndCommentsIfUserIsNotOwner() {
        ItemDtoWithBookingAndComments expected = new ItemDtoWithBookingAndComments(
                dryer.getId(),
                dryer.getName(),
                dryer.getDescription(),
                dryer.getAvailable(),
                null,
                null,
                new ArrayList<>()
        );
        lenient()
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockCommentRepository.findByItem_Id(dryer.getId()))
                .thenReturn(Collections.emptyList());
        ItemDtoWithBookingAndComments actual = itemService.getById(
                userOleg.getId(), dryer.getId()
        );

        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldSuccess() {
        Mockito
                .when(mockItemRepository.findAllByOwner_Id_OrderByIdAsc(userIrina.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        lenient()
                .when(mockBookingRepository.findByItem_IdInAndStartBeforeOrderByEndDesc(itemIds))
                .thenReturn(List.of(lastBooking, nextBooking));
        ItemDtoWithBookingAndComments expected = new ItemDtoWithBookingAndComments(
                dryer.getId(),
                dryer.getName(),
                dryer.getDescription(),
                dryer.getAvailable(),
                null,
                null,
                null
        );
        List<ItemDtoWithBookingAndComments> actual = itemService.getAll(userIrina.getId(), 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getAll_shouldSuccessIfCommentSizeNotNull() {
        Mockito
                .when(mockItemRepository.findAllByOwner_Id_OrderByIdAsc(userIrina.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        lenient()
                .when(mockBookingRepository.findByItem_IdInAndStartBeforeOrderByEndDesc(itemIds))
                .thenReturn(List.of(lastBooking, nextBooking));
        Mockito
                .when(mockCommentRepository.findByItem_IdIn(anySet()))
                .thenReturn(List.of(comment));
        ItemDtoWithBookingAndComments expected = new ItemDtoWithBookingAndComments(
                dryer.getId(),
                dryer.getName(),
                dryer.getDescription(),
                dryer.getAvailable(),
                null,
                null,
                List.of(commentDto)
        );
        List<ItemDtoWithBookingAndComments> actual = itemService.getAll(userIrina.getId(), 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getAll_shouldSuccessIfBookingsByItemNotNull() {
        Mockito
                .when(mockItemRepository.findAllByOwner_Id_OrderByIdAsc(userIrina.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        lenient()
                .when(mockBookingRepository.findByItem_IdInAndStartBeforeOrderByEndDesc(itemIds))
                .thenReturn(List.of(lastBooking, nextBooking));
        Mockito
                .when(mockCommentRepository.findByItem_IdIn(anySet()))
                .thenReturn(List.of(comment));
        Mockito
                .when(mockBookingRepository.findByItem_IdInAndStartBeforeOrderByEndDesc(anySet()))
                .thenReturn(List.of(lastBooking));
        Mockito
                .when(mockBookingRepository.findByItem_IdInAndStartAfterOrderByEndAsc(anySet()))
                .thenReturn(List.of(nextBooking));
        ItemDtoWithBookingAndComments expected = new ItemDtoWithBookingAndComments(
                dryer.getId(),
                dryer.getName(),
                dryer.getDescription(),
                dryer.getAvailable(),
                lastBookingShort,
                nextBookingShort,
                List.of(commentDto)
        );
        List<ItemDtoWithBookingAndComments> actual = itemService.getAll(userIrina.getId(), 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void update_shouldSuccess() {
        Item item = new Item(5L, "Bicycle", "With one wheel",
                true, userOleg, null);
        ItemDto itemUpdate = new ItemDto(item.getId(),
                "Tandem", "With three wheels",
                false, userDtoOleg, null);
        ItemDto expected = new ItemDto(
                item.getId(),
                itemUpdate.getName(),
                itemUpdate.getDescription(),
                itemUpdate.getAvailable(),
                itemUpdate.getOwner(),
                null
        );
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        lenient()
                .when(mockItemRepository.save(item))
                .thenReturn(item);
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        ItemDto actual = itemService.update(userOleg.getId(), item.getId(), itemUpdate);

        assertEquals(expected, actual);
    }

    @Test
    void updateWithNulls_shouldSuccess() {
        Item item = new Item(5L, "Bicycle", "With one wheel",
                true, userOleg, null);
        ItemDto itemUpdate = new ItemDto(item.getId(),
                null, null, null,
                userDtoOleg, null);
        ItemDto expected = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                itemUpdate.getOwner(),
                null
        );
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        lenient()
                .when(mockItemRepository.save(item))
                .thenReturn(item);
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        ItemDto actual = itemService.update(userOleg.getId(), item.getId(), itemUpdate);

        assertEquals(expected, actual);
    }

    @Test
    void update_shouldThrowExceptionIfUserIsNotOwner() {
        lenient()
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(
                        userOleg.getId(),
                        dryer.getId(),
                        new ItemDto(userIrina.getId(), null, null, null, null, null)
                )
        );
    }

    @Test
    void update_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(
                        userIrina.getId(),
                        99L,
                        new ItemDto(userIrina.getId(), null, null,
                                null, null, null)
                )
        );
    }

    @Test
    void delete_shouldSuccess() {
        Item apple = new Item(5L, "Cup", "Red", true, userOleg, null);
        Mockito
                .when(mockItemRepository.findById(apple.getId()))
                .thenReturn(Optional.of(apple));
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        itemService.deleteById(userOleg.getId(), apple.getId());

        Mockito
                .verify(mockItemRepository, Mockito.times(1))
                .deleteById(apple.getId());
    }

    @Test
    void delete_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.deleteById(userIrina.getId(), 99L));
    }

    @Test
    void delete_shouldThrowExceptionIfUserIsNotOwner() {
        lenient()
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));

        assertThrows(NotFoundException.class,
                () -> itemService.deleteById(userOleg.getId(), dryer.getId()));
    }

    @Test
    void deleteAll_shouldBeSuccess() {
        lenient()
                .when(mockItemRepository.findAll())
                .thenReturn(List.of(dryer));
        itemService.deleteAll();

        Mockito
                .verify(mockItemRepository, Mockito.times(1))
                .deleteAll();
    }

    @Test
    void getByText_ShouldSuccess() {
        Mockito
                .when(mockItemRepository.findByText("Dryer", PageRequest.of(0, 1)))
                .thenReturn(List.of(dryer));
        List<ItemDto> actual = itemService.getByText("Dryer", 0, 1);

        assertEquals(List.of(dryerDto), actual);
    }

    @Test
    void getByTextIfBlank_ShouldSuccess() {
        List<ItemDto> actual = itemService.getByText("", 0, 1);

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    void createComment_shouldSuccess() {
        Mockito
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockBookingRepository.findBookingsByBooker_IdAndItem_IdAndEndIsLessThanEqual(
                                eq(userOleg.getId()),
                                eq(dryer.getId()),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(List.of(lastBooking));
        Mockito.when(mockCommentRepository.save(any())).thenReturn(comment);
        CommentDto realComment = itemService.createComment(userOleg.getId(), dryer.getId(), commentDto);

        assertEquals(commentDto, realComment);
    }

    @Test
    void createComment_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(
                        userOleg.getId(),
                        99L,
                        commentDto
                )
        );
    }

    @Test
    void createComment_shouldThrowExceptionIfUserNotExist() {
        lenient()
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(
                        99L,
                        dryer.getId(),
                        commentDto
                )
        );
    }

    @Test
    void createComment_shouldThrowExceptionIfUserIsNotBooker() {
        Mockito
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockBookingRepository.findBookingsByBooker_IdAndItem_IdAndEndIsLessThanEqual(
                                eq(userOleg.getId()),
                                eq(dryer.getId()),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(Collections.emptyList());

        assertThrows(
                CommentAccessException.class,
                () -> itemService.createComment(userOleg.getId(), dryer.getId(), commentDto)
        );
    }
}