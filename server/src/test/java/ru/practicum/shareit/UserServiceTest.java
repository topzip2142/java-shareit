package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImp;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserRepository userRepository;
    private final UserServiceImp userServiceImp;

    @Test
    void getUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Юзер1");
        user1.setEmail("test1@gmail.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Юзер2");
        user2.setEmail("test2@gmail.com");
        userRepository.save(user2);

        List<UserResponseDTO> users = userServiceImp.getUsers().stream().toList();

        assertThat(users.size(), equalTo(2));
        assertThat(users.get(0).getId(), equalTo(user1.getId()));
        assertThat(users.get(0).getEmail(), equalTo(user1.getEmail()));
        assertThat(users.get(0).getName(), equalTo(user1.getName()));
        assertThat(users.get(1).getId(), equalTo(user2.getId()));
        assertThat(users.get(1).getEmail(), equalTo(user2.getEmail()));
        assertThat(users.get(1).getName(), equalTo(user2.getName()));
    }
}