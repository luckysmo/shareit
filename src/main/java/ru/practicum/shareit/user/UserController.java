package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Validated
@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody User user) {
        return userService.addNewUser(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @PatchMapping(value = "/{userID}")
    public UserDto patch(@PathVariable long userID,
                         @RequestBody User user) {
        return userService.update(userID, user);
    }

    @DeleteMapping("/{userID}")
    public void delete(@PathVariable long userID) {
        userService.delete(userID);
    }
}
