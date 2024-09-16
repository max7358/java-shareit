package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
class ItemRequestServiceTest {
    @Autowired
    ItemRequestService service;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;

    private ItemRequestDto requestDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = userService.createUser(new UserDto(null, "mail@du.tu", "name"));
        requestDto = new ItemRequestDto(null, "description", null, LocalDateTime.now(), List.of());
    }

    @Test
    void createRequest() {
        Long id = service.createRequest(requestDto, userDto.getId()).getId();

        TypedQuery<ItemRequest> query = em.createQuery("select request from ItemRequest request where request.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", id).getSingleResult();
        assertThat(request.getId(), equalTo(id));
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getRequestor().getId(), equalTo(userDto.getId()));
        assertThat(request.getRequestor().getId(), equalTo(userDto.getId()));
    }

    @Test
    void getRequest() {
        Long id = service.createRequest(requestDto, userDto.getId()).getId();

        ItemRequestDto requestDtoS = service.getRequest(id);
        assertThat(requestDtoS.getId(), equalTo(id));
        assertThat(requestDtoS.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestDtoS.getRequestorId(), equalTo(userDto.getId()));
    }

    @Test
    void getRequests() {
        Long id = service.createRequest(requestDto, userDto.getId()).getId();

        List<ItemRequestDto> requestDtoS = service.getRequests(userDto.getId());
        assertThat(requestDtoS.size(), equalTo(1));
        assertThat(requestDtoS.getFirst().getId(), equalTo(id));
        assertThat(requestDtoS.getFirst().getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestDtoS.getFirst().getRequestorId(), equalTo(userDto.getId()));
    }

    @Test
    void getAllRequests() {
        Long id = service.createRequest(requestDto, userDto.getId()).getId();

        List<ItemRequestDto> requestDtoS = service.getAllRequests(99L);
        assertThat(requestDtoS.size(), equalTo(1));
        assertThat(requestDtoS.getFirst().getId(), equalTo(id));
        assertThat(requestDtoS.getFirst().getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestDtoS.getFirst().getRequestorId(), equalTo(userDto.getId()));
    }
}
