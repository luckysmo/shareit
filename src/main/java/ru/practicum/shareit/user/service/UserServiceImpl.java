package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repo.UserRepoImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserRepoImpl userRepo;

    public UserDto addNewUser(User user) {
        if (userRepo.isExistEmail(user)) {
            userRepo.add(user);
            return mapper.mapToUserDto(user);
        } else {
            throw new RuntimeException("User already added!!!");
        }
    }

    public UserDto getById(long userId) {
        return mapper.mapToUserDto(userRepo.getById(userId)
                .get());
    }

    public UserDto update(long userID, User user) {
        User userExisting = userRepo.getById(userID).get();
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
            return mapper.mapToUserDto(userRepo.update(user));
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
            result.add(mapper.mapToUserDto(user));
        }
        return result;
    }
}
