package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void getByText_shouldSuccess() {
        User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
        userRepository.save(userOleg);
        Item item = itemRepository.save(new Item(1L, "Dryer", "For curly hair",
                true, userOleg, null));

        List<Item> result = itemRepository.findByText("rye", Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
    }
}