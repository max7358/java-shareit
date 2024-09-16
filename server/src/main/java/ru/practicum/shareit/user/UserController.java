package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Retrieving user with id: {}", id);
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Updating user: {}", userDto);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUserById(id);
    }
}
