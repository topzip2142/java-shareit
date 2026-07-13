package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto getUser(long id);

    Collection<UserDto> getUsers();

    UserDto createUser(NewUserRequest user);

    UserDto updateUser(long id, UpdateUserRequest user);

    void delUser(long id);
}