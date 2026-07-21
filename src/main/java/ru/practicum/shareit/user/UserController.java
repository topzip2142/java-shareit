package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserRequestDTO;
import ru.practicum.shareit.user.dto.UpdateUserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponseDTO get(@PathVariable long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public Collection<UserResponseDTO> getAll() {
        return userService.getUsers();
    }

    @PatchMapping("/{id}")
    public UserResponseDTO update(@PathVariable long id,
                                  @RequestBody @Valid UpdateUserRequestDTO user) {
        return userService.updateUser(id, user);
    }

    @PostMapping
    public UserResponseDTO create(@RequestBody @Valid NewUserRequestDTO user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delUser(id);
    }

}