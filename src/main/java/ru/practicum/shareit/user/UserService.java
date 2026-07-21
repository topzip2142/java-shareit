package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequestDTO;
import ru.practicum.shareit.user.dto.UpdateUserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;

import java.util.Collection;

public interface UserService {
    UserResponseDTO getUser(long id);

    Collection<UserResponseDTO> getUsers();

    UserResponseDTO createUser(NewUserRequestDTO user);

    UserResponseDTO updateUser(long id, UpdateUserRequestDTO user);

    void delUser(long id);
}