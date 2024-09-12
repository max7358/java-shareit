package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService service;

    @Autowired
    private EntityManager em;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "jon@mail.it", "John");
    }

    @Test
    void createUser() {
        service.createUser(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUser() {
        //given
        Long id = service.createUser(userDto).getId();
        //when
        UserDto userById = service.getUserById(id);
        //then
        assertThat(userById.getId(), equalTo(id));
        assertThat(userById.getName(), equalTo(userDto.getName()));
        assertThat(userById.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        Long id = service.createUser(userDto).getId();
        UserDto userDtoUpdated = new UserDto(null, "update@mail.it", "update");
        service.updateUser(id, userDtoUpdated);
        UserDto userById = service.getUserById(id);

        assertThat(userById.getId(), equalTo(id));
        assertThat(userById.getName(), equalTo(userDtoUpdated.getName()));
        assertThat(userById.getEmail(), equalTo(userDtoUpdated.getEmail()));
    }

    @Test
    void deleteUser() {
        Long id = service.createUser(userDto).getId();
        service.deleteUserById(id);
        assertThrows(NotFoundException.class, () -> service.getUserById(id));
    }
}
