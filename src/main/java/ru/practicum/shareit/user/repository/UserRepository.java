package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    Optional<User> findById(long id);

    List<User> findAll();

    User save(User user);

    User update(User user);

    void deleteById(long id);

    void deleteAll();

    Set<String> getEmails();
}
