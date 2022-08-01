package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private long id;
    @NotEmpty(groups = Class.class)
    private String name;
    @NotEmpty(groups = Create.class)
    @Email
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
