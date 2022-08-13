package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepoImpl implements UserRepo {

    private static long idCounter = 0;
    private final Map<Long, User> users = new HashMap<>();

    private void setIdCounter(User user) {
        ++idCounter;
        user.setId(idCounter);
    }

    public boolean isExistEmail(User user) {
        for (User value : users.values()) {
            if (value.getEmail().equals(user.getEmail())) {
                return false;
            }
        }
        return true;
    }

    public boolean isExist(long userId) {
        return users.containsKey(userId);
    }

    public User add(User user) {
        setIdCounter(user);
        users.put(user.getId(), user);
        log.debug("added {}", user);
        return users.get(user.getId());
    }

    public Optional<User> getById(Long userId) {
        log.debug("returned user with id {}", userId);
        return Optional.ofNullable(Optional.of(users.get(userId))
                .orElseThrow(() -> new NotFoundException("User not found!!!")));
    }

    public User update(User user) {
        users.replace(user.getId(), user);
        log.debug("user updated");
        return getById(user.getId()).orElseThrow(() -> new NotFoundException("User not found!!!"));
    }

    public void delete(long userID) {
        users.remove(userID);
        log.debug("deleted user with id {}", userID);
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}

