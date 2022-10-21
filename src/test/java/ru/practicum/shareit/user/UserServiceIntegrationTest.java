package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.UserMapper.mapToUser;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserDto userDto = new UserDto(null, "dto1", "email@mail.com");
    @Autowired
    UserService userService;

    @Test
    @DirtiesContext
    void testAddNewUser() {
        UserDto userInput = userService.addNewUser(userDto);

        User user = mapToUser(userInput);
        User userOutput = userService.getById(userInput.getId()).orElseThrow();

        assertEquals(user, userOutput);
    }

    @Test
    void testGetById() {
        User user = mapToUser(userService.addNewUser(userDto));

        User byId = userService.getById(user.getId()).orElseThrow();

        assertEquals(user, byId);
    }

    @Test
    void testUpdate() {
        UserDto input = new UserDto(null, "UPDATE", null);
        User user = mapToUser(userService.addNewUser(userDto));

        userService.update(user.getId(), input);

        assertEquals(input.getName(), "UPDATE");
    }

    @Test
    void testGetAll() {
        userService.addNewUser(userDto);

        List<UserDto> users = userService.getAll();

        assertThat(users.size(), equalTo(1));
    }

}
