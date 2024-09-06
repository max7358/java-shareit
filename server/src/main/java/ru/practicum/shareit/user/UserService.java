package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto getUserById(long id) {
        Optional<User> user = repository.findById(id);
        return UserMapper.toUserDto(user.orElseThrow(() -> new NotFoundException("User with id:" + id + " not found")));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = repository.findById(id).orElseThrow(() -> new NotFoundException("User with id:" + id + " not found"));
        if (userDto.getEmail() != null) {
            if (!userToUpdate.getEmail().equals(userDto.getEmail())) {
                validateEmail(userDto.getEmail());
            }
            userToUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        User user = repository.save(userToUpdate);
        return UserMapper.toUserDto(user);
    }

    private void validateEmail(String email) {
        repository.findByEmail(email).ifPresent(s -> {
            throw new ConflictException("User with email " + email + " already exists");
        });
    }

    @Transactional
    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }
}
