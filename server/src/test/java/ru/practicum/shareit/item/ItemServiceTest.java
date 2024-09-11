package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService service;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;

    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = userService.createUser(new UserDto(null, "mail@du.tu", "name"));
        itemDto = new ItemDto(null, "item", "item descr", true, null, userDto.getId());
    }

    @Test
    void createItem() {
        service.createItem(userDto.getId(), itemDto);
        TypedQuery<Item> query = em.createQuery("select item from Item item where item.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();
        assertThat(item.getId(), notNullValue());
    }

    @Test
    void getItem() {
        ItemDto itemDtoT = service.createItem(userDto.getId(), itemDto);
        ItemDto itemS = service.getItem(itemDtoT.getId(), userDto.getId());
        assertThat(itemS.getId(), equalTo(itemDtoT.getId()));
    }
}
