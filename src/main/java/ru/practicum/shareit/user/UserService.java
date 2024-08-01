package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User getUserById(long id) {
            return userStorage.findById(id);
    }

    public User updateUser(Long id, User user) {
        return userStorage.update(id, user);
    }

    public void deleteUserById(Long id) {
        userStorage.delete(id);
    }
}
