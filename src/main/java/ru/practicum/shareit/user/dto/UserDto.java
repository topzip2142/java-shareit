package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String name;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto dto = (UserDto) o;
        return id == dto.id && Objects.equals(name, dto.name) && Objects.equals(email, dto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }
}