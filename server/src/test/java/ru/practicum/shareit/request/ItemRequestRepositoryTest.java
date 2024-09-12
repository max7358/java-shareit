package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findById() {
        User user = new User(null, "mail@du.tu", "name");
        ItemRequest request = new ItemRequest(null, "description", user, LocalDateTime.now());
        em.persist(user);
        em.persist(request);
        em.flush();

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId());
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.getFirst(), equalTo(request));
    }

    @Test
    void findByIdNotRequestor() {
        User user = new User(null, "mail@du.tu", "name");
        ItemRequest request = new ItemRequest(null, "description", user, LocalDateTime.now());
        em.persist(user);
        em.persist(request);
        em.flush();

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(99L);
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.getFirst(), equalTo(request));
    }
}
