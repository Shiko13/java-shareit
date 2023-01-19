package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItem_Id(long itemId);

    List<Comment> findByItem_IdIn(Set<Long> itemsId);
}
