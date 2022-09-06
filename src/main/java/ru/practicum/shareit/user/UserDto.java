package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotEmpty(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class})
    @NotEmpty(groups = {Create.class})
    private String email;
}
