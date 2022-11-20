package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    void deleteById(Long userId);

    Collection<User> findAll();

    void update(User user);

    Optional<User> findById(Long userId);

    Boolean existsById(Long userId);

    Boolean existsByEmail(String email);

}
