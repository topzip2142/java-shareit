package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUser(long id);

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    void delUser(long id);
}