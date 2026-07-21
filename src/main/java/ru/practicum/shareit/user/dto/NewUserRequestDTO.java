package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserRequestDTO {
    private String name;
    @Email
    private String email;
}