package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoConverter;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyIdAndBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoConverter;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoConverter;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoWithBookingAndComments> getAll(long sharerId, int from, int size) {
        log.debug("Start request GET to /items");
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));

        List<Item> items = itemRepository.findAllByOwner_Id_OrderByIdAsc(sharerId,
                PageRequest.of(from / size, size));
        List<ItemDtoWithBookingAndComments> itemDtoWithBookingAndComments = new ArrayList<>();
        Set<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toSet());
        List<Comment> comments = commentRepository.findByItem_IdIn(itemsId);
        Map<Item, List<Comment>> commentsByItem = comments.stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<Booking> lastBookings = bookingRepository.findByItem_IdInAndStartIsLessThanEqualOrderByEndDesc(itemsId, LocalDateTime.now());
        Map<Item, List<Booking>> bookingsByItem = lastBookings.stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<Booking> nextBookings = bookingRepository.findByItem_IdInAndStartAfterOrderByEndAsc(itemsId, LocalDateTime.now());
        Map<Item, List<Booking>> bookingsByItem2 = nextBookings.stream()
                .collect(groupingBy(Booking::getItem, toList()));

        fillItemDtoWithBookingAndComments(items, itemDtoWithBookingAndComments, commentsByItem, bookingsByItem, bookingsByItem2);

        return itemDtoWithBookingAndComments;
    }


    @Override
    public ItemDtoWithBookingAndComments getById(long sharerId, long id) {
        log.debug("Start request GET to /items/{}", id);
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " not found"));

        return getItemDtoWithBookingAndComments(sharerId, item);
    }

    @Override
    public List<ItemDto> getByText(String text, int from, int size) {
        log.debug("Start request GET to /items/search?text={}", text);
        return itemRepository.findByText(text, PageRequest.of(from / size, size))
                .stream()
                .map(ItemDtoConverter::toDto)
                .collect(toList());
    }

    @Override
    public ItemDto create(long sharerId, ItemDtoInput itemDto) {
        log.debug("Start request POST to /items, with sharerId = {}, id = {}, name = {}, description = {}, isAvailable = {}",
                sharerId, itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        User owner = userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));

        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = null;

        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() ->
                            new NotFoundException("This itemRequest not be found"));
        }

        Item item = ItemDtoConverter.fromDtoInput(itemDto, owner, itemRequest);

        return ItemDtoConverter.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long sharerId, long id, ItemDto itemDto) {
        log.debug("Start request PATCH to /items/{}", id);
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " not found"));

        if (!item.getOwner().getId().equals(sharerId)) {
            throw new NotFoundException("Item with this id not found in this user");
        }
        itemDto.setId(id);

        return ItemDtoConverter.toDto(update(itemDto, item));
    }

    @Override
    public void deleteById(long sharerId, long id) {
        log.debug("Start request DELETE to /items/{}", id);
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));
        itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " not found"));
        itemRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /items)");
        itemRepository.deleteAll();
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        log.debug("Start request POST to /items/{}/comment", itemId);
        User author = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + itemId + " not found"));

        List<Booking> bookings = bookingRepository.findBookingsByBooker_IdAndItem_IdAndEndIsLessThanEqual(userId,
                itemId, LocalDateTime.now());
        if (bookings.stream().findAny().isEmpty()) {
            throw new CommentAccessException("You are not booked this item");
        }

        Comment comment = CommentDtoConverter.fromDto(commentDto, item, author);
        commentRepository.save(comment);

        return CommentDtoConverter.toDto(comment);
    }

    private Item update(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !item.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    private ItemDtoWithBookingAndComments getItemDtoWithBookingAndComments(long sharerId, Item item) {
        BookingDtoOnlyIdAndBookerId lastBooking = null;
        BookingDtoOnlyIdAndBookerId nextBooking = null;
        if (item.getOwner().getId() == sharerId) {
            lastBooking = bookingRepository.findFirstByItem_IdAndStartIsLessThanEqualOrderByEndAsc(item.getId(),
                            LocalDateTime.now())
                    .map(BookingDtoConverter::toDtoOnlyIdAndBookerId)
                    .orElse(null);

            nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterOrderByEndAsc(item.getId(),
                            LocalDateTime.now())
                    .map(BookingDtoConverter::toDtoOnlyIdAndBookerId)
                    .orElse(null);
        }
        List<CommentDto> comments = commentRepository.findByItem_Id(item.getId()).stream()
                .map(CommentDtoConverter::toDto)
                .collect(toList());
        return ItemDtoConverter.toDtoWithBookingAndComments(item, lastBooking, nextBooking, comments);
    }

    private void fillItemDtoWithBookingAndComments(List<Item> items,
                                                   List<ItemDtoWithBookingAndComments> itemDtoWithBookingAndComments,
                                                   Map<Item, List<Comment>> commentsByItem,
                                                   Map<Item, List<Booking>> bookingsByItem,
                                                   Map<Item, List<Booking>> bookingsByItem2) {
        for (Item item : items) {
            List<CommentDto> commentsDto = null;
            BookingDtoOnlyIdAndBookerId lastBooking = null;
            BookingDtoOnlyIdAndBookerId nextBooking = null;

            if (commentsByItem.size() != 0) {
                commentsDto = commentsByItem.get(item).stream()
                        .map(CommentDtoConverter::toDto)
                        .collect(toList());
            }

            if (bookingsByItem.get(item) != null) {
                List<Booking> lastBookings = bookingsByItem.get(item);

                if (lastBookings.size() != 0) {
                    lastBooking = BookingDtoConverter.toDtoOnlyIdAndBookerId(lastBookings.get(0));
                }
            }

            if (bookingsByItem2.get(item) != null) {
                List<Booking> nextBookings = bookingsByItem2.get(item);

                if (nextBookings.size() != 0) {
                    nextBooking = BookingDtoConverter.toDtoOnlyIdAndBookerId(nextBookings.get(0));
                }
            }

            itemDtoWithBookingAndComments.add(ItemDtoConverter.toDtoWithBookingAndComments(
                    item, lastBooking, nextBooking, commentsDto
            ));
        }
    }
}
