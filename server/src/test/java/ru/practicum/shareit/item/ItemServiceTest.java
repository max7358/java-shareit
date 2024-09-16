package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentSaveDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService service;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;

    @MockBean
    private BookingService bookingService;

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
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(itemDto.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getItem() {
        Long id = service.createItem(userDto.getId(), itemDto).getId();

        ItemDto itemS = service.getItem(id, userDto.getId());
        assertThat(itemS.getId(), equalTo(id));
        assertThat(itemS.getName(), equalTo(itemDto.getName()));
        assertThat(itemS.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemS.getOwnerId(), equalTo(itemDto.getOwnerId()));
        assertThat(itemS.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void updateItem() {
        Long id = service.createItem(userDto.getId(), itemDto).getId();
        ItemDto itemUpdateDto = ItemDto.builder().name("New Name").build();
        service.updateItem(itemDto.getOwnerId(), id, itemUpdateDto);

        ItemDto itemS = service.getItem(id, userDto.getId());
        assertThat(itemS.getId(), equalTo(id));
        assertThat(itemS.getName(), equalTo(itemUpdateDto.getName()));
        assertThat(itemS.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemS.getOwnerId(), equalTo(itemDto.getOwnerId()));
        assertThat(itemS.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getItems() {
        Long id = service.createItem(userDto.getId(), itemDto).getId();

        List<ItemAllDto> itemS = service.getItems(userDto.getId());
        assertThat(itemS.size(), equalTo(1));
        assertThat(itemS.getFirst().getId(), equalTo(id));
    }

    @Test
    void findItems() {
        Long id = service.createItem(userDto.getId(), itemDto).getId();

        List<ItemDto> itemS = service.findItems(itemDto.getDescription().replaceFirst(".$", ""));
        assertThat(itemS.size(), equalTo(1));
        assertThat(itemS.getFirst().getId(), equalTo(id));
    }

    @Test
    void findItemsEmpty() {
        Long id = service.createItem(userDto.getId(), itemDto).getId();

        List<ItemDto> itemS = service.findItems("");
        assertThat(itemS.size(), equalTo(0));
    }

    @Test
    void addComment() {
        when(bookingService.getBookingsByUserAndItem(anyLong(), anyLong())).thenReturn(List.of(new BookingDto()));

        Long id = service.createItem(userDto.getId(), itemDto).getId();
        CommentSaveDto commentSaveDto = new CommentSaveDto("comment text");
        service.addComment(itemDto.getOwnerId(), id, commentSaveDto);

        ItemAllDto itemS = service.getItem(id, userDto.getId());
        assertThat(itemS.getId(), equalTo(id));
        assertThat(itemS.getComments().getFirst().getItemId(), equalTo(id));
        assertThat(itemS.getComments().getFirst().getText(), equalTo(commentSaveDto.getText()));
    }

    @Test
    void addCommentException() {
        when(bookingService.getBookingsByUserAndItem(anyLong(), anyLong())).thenReturn(List.of());

        Long id = service.createItem(userDto.getId(), itemDto).getId();
        CommentSaveDto commentSaveDto = new CommentSaveDto("comment text");
        assertThrows(BadRequestException.class, () -> service.addComment(itemDto.getOwnerId(), id, commentSaveDto));
    }
}
