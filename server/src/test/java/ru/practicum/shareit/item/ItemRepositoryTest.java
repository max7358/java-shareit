package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void search() {
        User user = new User(null, "mail@du.tu", "name");
        Item item = new Item(null, "item", "item descr", true, user, null);
        em.persist(user);
        em.persist(item);
        em.flush();

        List<Item> items = itemRepository.search("te");
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst(), equalTo(item));
    }

    @Test
    void findByRequestId() {
        User user = new User(null, "mail@du.tu", "name");
        ItemRequest request = new ItemRequest(null, "description", user, LocalDateTime.now());
        Item item = new Item(null, "item", "item descr", true, user, request);
        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.flush();

        List<Item> items = itemRepository.findByRequestId(request.getId());
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst(), equalTo(item));
    }

    @Test
    void findAllByRequestIn() {
        User user = new User(null, "mail@du.tu", "name");
        ItemRequest request = new ItemRequest(null, "description", user, LocalDateTime.now());
        Item item = new Item(null, "item", "item descr", true, user, request);
        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.flush();

        List<Item> items = itemRepository.findAllByRequestIn(List.of(request));
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst(), equalTo(item));
    }
}
