package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    private final UserDto userDto = new UserDto(1L, "userDto1", "userDto1@mail.com");
    private final User user = new User(1L, "user", "user@mail.com");
    private final User user2 = new User(2L, "user2", "user2@mail.com");
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void whenCreate_thenCallUserRepository() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(user);

        userService.addNewUser(userDto);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void whenUpdateUser_thenCorrectObjectShouldBeSaved() {
        UserDto inputUserDto = new UserDto(null, "UpdateUser1", null);
        User updatedUser = new User(1L, "UpdateUser1", "user1@mail.ru");

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.update(1L, inputUserDto);

        assertEquals(Optional.of(updatedUser), userRepository.findById(1L));
    }

    @Test
    public void whenUpdateUserName_thenReturnUpdatedUserWithUpdatedName() {
        UserDto inputUserDto = new UserDto(null, "UpdateUser1", null);

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.update(anyLong(), inputUserDto);

        assertEquals("UpdateUser1", userDto.getName());
    }

    @Test
    public void whenUpdateEmail_thenReturnUpdatedUserWithUpdatedEmail() {
        UserDto inputUserDto = new UserDto(null, null, "userUpdated1@mail.ru");

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.update(anyLong(), inputUserDto);

        assertEquals("userUpdated1@mail.ru", userDto.getEmail());
    }

    @Test
    public void whenGetUserByIdExist_thenCallUserRepository() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.getById(anyLong());

        Mockito.verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    public void whenGetUserByIdNotExist_thenThrowNotFoundException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getById(-99L));

        assertEquals("User not found!!!", exception.getMessage());
    }


    @Test
    public void whenDeleteById_thenCallUserRepository() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.delete(anyLong());

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void whenDeleteByIdWithWrongId_thenCallException() {

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.delete(anyLong()));

        assertEquals("User not found!!!", notFoundException.getMessage());
    }

    @Test
    public void whenGetAllUsers_thenCallUserRepository() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user, user2));

        userService.getAll();

        Mockito.verify(userRepository, times(1))
                .findAll();
    }

    @Test
    public void whenGetAllUsers_thenReturnUsersList() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user, user2));

        List<UserDto> users = userService.getAll();

        assertEquals(2, users.size());
    }

}

