package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    User userIrina = new User(2L, "Irina", "irina@yandex.ru");
    Item dryer = new Item(3L, "Dryer", "For curly hair",
            true, userOleg, null);
    private final Comment comment = new Comment(
            4L,
            "Hot!",
            dryer,
            userIrina,
            LocalDateTime.of(2023, 1, 20, 12, 0, 0)
    );
    private final Set<Long> itemIds = new HashSet<>(Arrays.asList(1L, 3L));

    @Test
    void findByItemId_shouldSuccess() {
        List<Comment> result = commentRepository.findByItem_Id(dryer.getId());

        assertThat(result).isNotEmpty();
        assertEquals(result.get(0).getId(), comment.getId());
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(0).getAuthor().getId(), comment.getAuthor().getId());
        assertEquals(result.get(0).getAuthor().getName(), comment.getAuthor().getName());
        assertEquals(result.get(0).getCreated(), comment.getCreated());
    }

    @Test
    void findByItemIdInSet_shouldSuccess() {
        List<Comment> result = commentRepository.findByItem_IdIn(itemIds);

        assertThat(result).isNotEmpty();
        assertEquals(result.get(0).getId(), comment.getId());
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(0).getAuthor().getId(), comment.getAuthor().getId());
        assertEquals(result.get(0).getAuthor().getName(), comment.getAuthor().getName());
        assertEquals(result.get(0).getCreated(), comment.getCreated());
    }
}