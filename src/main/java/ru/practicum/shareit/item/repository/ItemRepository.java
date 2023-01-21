package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item as i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%') ) ) " +
            "or (upper(i.description) like upper(concat('%', ?1, '%') ) ) " +
            "and i.available = true")
    List<Item> findByText(String text);

    List<Item> findAllByOwner_Id_OrderByIdAsc(long ownerId);
}
