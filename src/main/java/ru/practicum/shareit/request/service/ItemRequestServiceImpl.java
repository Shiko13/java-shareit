package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoConverter;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoConverter;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestDtoOutput> getAll(long requestorId) {
        log.debug("Start request GET to /requests");
        userRepository.findById(requestorId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + requestorId + " not found"));
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_Id(requestorId);

        return getItemRequestsDtoWithItemsFromRequests(requests);
    }

    @Override
    public ItemRequestDtoOutput getById(long userId, long requestId) {
        log.debug("Start request GET to /requests/{}", requestId);
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new NotFoundException("Request with id = " + userId + " not found"));

        List<Item> items = new ArrayList<>(itemRepository.findByRequest_IdOrderById(requestId));

        return ItemRequestDtoConverter.toDtoOutput(itemRequest, ItemDtoConverter.toDtoListForRequest(items));
    }

    @Override
    public List<ItemRequestDtoOutput> getAllAnotherUsers(long requestorId, int from, int size) {
        log.debug("Start request GET to /requests/all");
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdNot(requestorId,
                PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "created")));

        return getItemRequestsDtoWithItemsFromRequests(requests);
    }

    @Override
    public ItemRequestDtoOutput create(long requestorId, ItemRequestDtoInput itemRequestDtoInput) {
        log.debug("Start request POST to /requests");
        User owner = userRepository.findById(requestorId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + requestorId + " not found"));

        ItemRequest itemRequest = ItemRequestDtoConverter.fromDtoInput(itemRequestDtoInput, owner);
        itemRequestRepository.save(itemRequest);

        return ItemRequestDtoConverter.toDtoOutput(itemRequest, null);
    }

    private List<ItemRequestDtoOutput> getItemRequestsDtoWithItemsFromRequests(List<ItemRequest> requests) {
        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequest_IdIn(requestsId);

        return requests.stream()
                .map(i -> ItemRequestDtoConverter.toDtoOutput(i, getItemsByRequestId(i.getId(), items)))
                .collect(Collectors.toList());
    }

    private List<ItemDtoForRequest> getItemsByRequestId(long requestId, List<Item> items) {
        List<Item> itemsForMapping = items
                .stream()
                .filter(item -> item.getRequest().getId() == requestId)
                .collect(Collectors.toList());

        return ItemDtoConverter.toDtoListForRequest(itemsForMapping);
    }
}