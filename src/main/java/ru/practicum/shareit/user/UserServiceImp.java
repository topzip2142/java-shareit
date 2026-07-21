package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + id + " не найден"));
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ValidationException("Email должен быть указан");
        }
        if (isEmailExist(request.getEmail())) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UpdateUserRequest userFields) {
        if (userFields.hasEmail() && isEmailExist(userFields.getEmail())) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
        User updatedUser = userRepository.findById(id)
                .map(user -> UserMapper.updateFields(user, userFields))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + id + " не найден"));
        updatedUser = userRepository.save(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void delUser(long id) {
        userRepository.findById(id).ifPresentOrElse(
                user -> userRepository.deleteById(id),
                () -> {
                    throw new NotFoundException("Пользователь с ID: " + id + " не найден");
                }
        );
    }

    private boolean isEmailExist(String email) {
        return !userRepository.findByEmailContainingIgnoreCase(email).isEmpty();
    }
}