package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User user = userStorage.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto getUserById(long id) {
            return UserMapper.toUserDto(userStorage.findById(id));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userStorage.update(id, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public void deleteUserById(Long id) {
        userStorage.delete(id);
    }
}
