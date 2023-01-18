package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoConverter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    @Override
    public List<UserDto> getAll() {
        log.debug("Start request GET to /users");
        return userRepository.findAll()
                .stream()
                .map(userDtoConverter::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public UserDto getById(long id) {
        log.debug("Start request GET to /users/{}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + id + " not found"));
        return userDtoConverter.toDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Start request POST to /users, with id = {}, name = {}, email = {}",
                userDto.getId(), userDto.getName(), userDto.getEmail());
        User user = userDtoConverter.fromDto(userDto);
        return userDtoConverter.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto userDto) {
        log.debug("Start request PATCH to /users, with id = {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                new NotFoundException("User with id = " + id + " not found"));
        userDto.setId(id);
        return userDtoConverter.toDto(update(userDto, user));
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /users/{}", id);
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /users)");
        userRepository.deleteAll();
    }

    private User update(UserDto userDto, User user) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        return user;
    }
}
