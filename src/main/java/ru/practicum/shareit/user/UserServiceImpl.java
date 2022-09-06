package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.mapToUser;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto addNewUser(UserDto userDto) {
        return mapToUserDto(repository.save(mapToUser(userDto)));
    }

    @Transactional(readOnly = true)
    public Optional<User> getById(long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!!!"));
        return Optional.of(user);
    }

    @Transactional
    public UserDto update(long userID, UserDto userDto) {
        User userExisting = repository.findById(userID).orElseThrow(() -> new NotFoundException("User not found!!!"));
        User user = mapToUser(userDto);
        if (repository.existsById(userID)) {
            user.setId(userID);
            if (user.getEmail() == null) {
                user.setEmail(userExisting.getEmail());
            }
            if (user.getName() == null) {
                if (repository.findUserByEmail(user.getEmail()) == null) {
                    user.setName(userExisting.getName());
                } else {
                    throw new IllegalArgumentException("Email exists!!!");
                }
            }
            return mapToUserDto(repository.save(user));
        } else {
            throw new NotFoundException("User not found!!!");
        }
    }

    @Transactional
    public void delete(long userID) {
        if (repository.existsById(userID)) {
            repository.deleteById(userID);
        } else {
            throw new NotFoundException("User not found!!!");

        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        List<UserDto> result = new ArrayList<>();
        for (User user : repository.findAll()) {
            result.add(mapToUserDto(user));
        }
        return result;
    }
}
