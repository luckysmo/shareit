package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo {
    boolean isExistEmail(User user);

    boolean isExist(long userId);

    User add(User user);

    Optional<User> getById(Long userId);

    User update(User user);

    void delete(long userID);

    List<User> getAll();
}
