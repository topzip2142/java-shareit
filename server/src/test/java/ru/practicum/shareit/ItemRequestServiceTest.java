package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImp;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestServiceImp itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Test
    void getAllItemRequestsTest() {
        User user1 = new User();
        user1.setName("Ivan");
        user1.setEmail("test1@mail.ru");

        User user2 = new User();
        user2.setName("John");
        user2.setEmail("test2@mail.ru");

        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("Описание первого запроса");
        itemRequest1.setCreated(LocalDateTime.now().withNano(0));
        itemRequest1.setAuthor(user1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("Описание второго запроса");
        itemRequest2.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        itemRequest2.setAuthor(user2);

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequestDTO> itemRequests = itemRequestService.getAllItemRequests();

        assertThat(itemRequests.size(), equalTo(2));
        assertThat(itemRequests.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequests.get(0).getCreated(), equalTo(itemRequest1.getCreated()));
        assertThat(itemRequests.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(itemRequests.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(itemRequests.get(1).getCreated(), equalTo(itemRequest2.getCreated()));
    }
}