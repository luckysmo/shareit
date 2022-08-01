package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private long id;
    @NotEmpty(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class})
    @NotEmpty(groups = {Create.class})
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
