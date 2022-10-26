package ru.practicum.shareit.user;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ru.practicum.shareit.user.UserMapper.mapToUser;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

public class UserMapperTest {


    @Test
    void testMapToUser() {
        UserDto userDto = new UserDto(1L, "NAME", "email@mail.com");

        User user = mapToUser(userDto);

        Assertions.assertEquals(new User(1L, "NAME", "email@mail.com"), user);
    }

    @Test
    void testMapToUserDto() {
        User user = new User(1L, "NAME", "email@mail.com");

        UserDto userDto = mapToUserDto(user);

        Assertions.assertEquals(new UserDto(1L, "NAME", "email@mail.com"), userDto);
    }
}
