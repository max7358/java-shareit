package ru.practicum.shareit.common.storage;

import java.util.List;

public interface CommonStorage<T> {
    T create(T t);

    T update(Long id, T t);

    List<T> findAll();

    T findById(Long id);

    void delete(Long id);
}
