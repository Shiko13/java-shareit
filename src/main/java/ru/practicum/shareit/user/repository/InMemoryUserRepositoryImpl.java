package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long count = 1;

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        emailCheckDuplicate(user.getEmail());
        user.setId(count++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }


    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            String oldEmail = users.get(user.getId()).getEmail();
            String newEmail = user.getEmail();
            if (!oldEmail.equals(newEmail)) {
                emailCheckDuplicate(newEmail);
                emails.remove(oldEmail);
                emails.add(newEmail);
            }
            users.replace(user.getId(), user);
        } else {
            throw new NotFoundException("User with this id not found");
        }
        return user;
    }

    @Override
    public void deleteById(long id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public void deleteAll() {
        emails.clear();
        users.clear();
    }

    public Set<String> getEmails() {
        return emails;
    }

    private void emailCheckDuplicate(String user) {
        if (emails.contains(user)) {
            throw new ServerException("This email already has been registered");
        }
    }
}
