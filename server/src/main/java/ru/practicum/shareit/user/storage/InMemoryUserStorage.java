package ru.practicum.shareit.user.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@Deprecated
public class InMemoryUserStorage implements UserStorage {

    @Getter
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User create(User user) {
        validateEmailExists(user.getEmail());
        user.setId(id++);
        users.put(user.getId(), user);
        log.debug("User created: {}", user);
        return user;
    }

    private void validateEmailExists(String email) {
        users.entrySet().stream().filter(entry -> email.equals(entry.getValue().getEmail())).findFirst()
                .ifPresent(s -> {
                    throw new ConflictException("User with email " + email + " already exists");
                });
    }

    @Override
    public User update(Long id, User user) {
        if (users.containsKey(id)) {
            User userToUpdate = users.get(id);
            if (user.getEmail() != null) {
                if (!userToUpdate.getEmail().equals(user.getEmail())) {
                    validateEmailExists(user.getEmail());
                }
                userToUpdate.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                userToUpdate.setName(user.getName());
            }
            users.put(id, userToUpdate);
            log.debug("User updated: {}", userToUpdate);
            return userToUpdate;
        } else {
            throw new NotFoundException("User with id:" + user.getId() + " not found");
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() -> new NotFoundException("User with id:" + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(users.remove(id)).orElseThrow(() -> new NotFoundException("User with id:" + id + " not found"));
    }
}
