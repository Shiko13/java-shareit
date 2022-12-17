package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoConverter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemDtoConverter itemDtoConverter;

    @Override
    public List<ItemDto> getAll(long sharerId) {
        log.debug("Start request GET to /items");
        return itemRepository.findAll(sharerId)
                .stream()
                .map(itemDtoConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(long id) {
        log.debug("Start request GET to /items/{}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " not found"));
        return itemDtoConverter.toDto(item);
    }

    @Override
    public List<ItemDto> getByText(String text) {
        log.debug("Start request GET to /items/search?text={}", text);
        return itemRepository.findByText(text)
                .stream()
                .map(itemDtoConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long sharerId, ItemDto itemDto) {
        log.debug("Start request POST to /items, with sharerId = {}, id = {}, name = {}, description = {}, isAvailable = {}",
                sharerId, itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));
        Item item = itemDtoConverter.fromDto(sharerId, itemDto);
        return itemDtoConverter.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long sharerId, long id, ItemDto itemDto) {
        log.debug("Start request PATCH to /items, with id = {}", id);
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " not found"));
        itemDto.setId(id);
        if (item.getSharerId() != sharerId) {
            throw new NotFoundException("Item with this id not found in this user");
        }
        return itemDtoConverter.toDto(update(itemDto, item));
    }

    @Override
    public void deleteById(long sharerId, long id) {
        log.debug("Start request DELETE to /items/{}", id);
        itemRepository.deleteById(sharerId, id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /items)");
        itemRepository.deleteAll();
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
}
