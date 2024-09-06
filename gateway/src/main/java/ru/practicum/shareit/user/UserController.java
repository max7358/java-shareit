package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validate.OnCreate;
import ru.practicum.shareit.validate.OnUpdate;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    @Validated(OnUpdate.class)
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Updating user: {}", userDto);
        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Retrieving user with id: {}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        return userClient.deleteUserById(id);
    }
}
