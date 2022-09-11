package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import java.util.List;
import java.util.Optional;

@Validated
@RequestMapping("/users")
@RestController
public class UserController {
    final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        return userService.addNewUser(userDto);
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @PatchMapping(value = "/{userID}")
    public UserDto patch(@PathVariable long userID,
                         @RequestBody @Validated(Update.class) UserDto userDto) {
        return userService.update(userID, userDto);
    }

    @DeleteMapping("/{userID}")
    public void delete(@PathVariable long userID) {
        userService.delete(userID);
    }
}
