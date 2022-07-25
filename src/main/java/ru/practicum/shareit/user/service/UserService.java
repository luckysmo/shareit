package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNewUser(User user);

    UserDto getById(long userId);

    UserDto update(long userId, User user);

    void delete(long userId);

    List<UserDto> getAll();
}
