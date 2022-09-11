package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto addNewUser(UserDto userDto);

    Optional<User> getById(long userId);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);

    List<UserDto> getAll();
}
