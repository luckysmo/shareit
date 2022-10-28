package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Adding new user with name {} and email {} .", userDto.getName(), userDto.getEmail());
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> editUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        log.info("Editing user with id = {}", userId);
        userDto.setId(userId);
        return client.update(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long userId) {
        log.info("Getting user by id = {}", userId);
        return client.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return client.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Deleting user with id = {}", userId);
        client.delete(userId);
    }
}