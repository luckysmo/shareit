package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repo.UserRepoImpl;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepoImpl userRepo;

    public UserServiceImpl(UserRepoImpl userRepo) {
        this.userRepo = userRepo;
    }

    public UserDto addNewUser(User user) {
        if (userRepo.isExistEmail(user)) {
            userRepo.add(user);
            return mapToUserDto(user);
        } else {
            throw new RuntimeException("User already added!!!");
        }
    }

    public UserDto getById(long userId) {
        return mapToUserDto(userRepo.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found!!!")));
    }

    public UserDto update(long userID, User user) {
        User userExisting = userRepo.getById(userID).orElseThrow(() -> new NotFoundException("User not found!!!"));
        if (userRepo.isExist(userID)) {
            user.setId(userID);
            if (user.getEmail() == null) {
                user.setEmail(userExisting.getEmail());
            }
            if (user.getName() == null) {
                if (userRepo.isExistEmail(user)) {
                    user.setName(userExisting.getName());
                } else {
                    throw new IllegalArgumentException("Email exists!!!");
                }
            }
            return mapToUserDto(userRepo.update(user));
        } else {
            throw new NotFoundException("User not found!!!");
        }
    }

    public void delete(long userID) {
        if (userRepo.isExist(userID)) {
            userRepo.delete(userID);
        } else {
            throw new NotFoundException("User not found!!!");

        }
    }

    public List<UserDto> getAll() {
        List<UserDto> result = new ArrayList<>();
        for (User user : userRepo.getAll()) {
            result.add(mapToUserDto(user));
        }
        return result;
    }
}
