package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Validator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long idUser = 0L;

    @Override
    public User save(User user) {

        Validator.userValidation(user, users);

        user.setId(++idUser);
        users.put(user.getId(), user);

        log.info("Добавление пользователя: {}", user);

        return user;

    }

    @Override
    public void deleteById(Long userId) {

        users.remove(userId);

    }

    @Override
    public Optional<User> findById(Long userId) {

        log.info("Получение пользователя по id.");

        return Optional.ofNullable(users.get(userId));

    }

    @Override
    public Boolean existsById(Long userId) {

        return users.get(userId).equals(null);

    }

    @Override
    public Boolean existsByEmail(String email) {

        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public Collection<User> findAll() {

        log.info("Получение всех пользователей.");
        return users.values();

    }

    @Override
    public void update(User user) {

        Validator.userValidation(user, users);

        users.remove(user.getId());
        users.put(user.getId(), user);

        log.info("Обновленние пользователя: {}", user);

    }

}

