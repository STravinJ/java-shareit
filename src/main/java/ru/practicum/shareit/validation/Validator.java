package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import java.util.Map;

public class Validator {

    public static void userValidation(User user, Map<Long, User> users) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ @.");
        }

        if (user.getId() != null && !users.containsKey(user.getId())) {
            throw new DataNotFoundException("Не найден пользователь при обновлении.");
        }

        for (User userV : users.values()) {
            if (!user.getId().equals(userV.getId())
                    && userV.getEmail().equals(user.getEmail())) {
                throw new DataNotFoundException("Пользователь с такой почтой уже существует.");
            }
        }

    }

    public static void userIdValidation(Long userID, Map<Long, User> users) {

        if (!(users.containsKey(userID))) {
            String errorMsg = String.format("Отсутствует пользователь с id=%s", userID);
            throw new DataNotFoundException(errorMsg);
        }
        if (userID < 0) {
            String errorMsg = String.format("Некорректный id=%s", userID);
            throw new ValidationException(errorMsg);
        }

    }

    public static void userIdValidation(Long userID) {

        if (userID < 0) {
            String errorMsg = String.format("Некорректный id=%s", userID);
            throw new DataNotFoundException(errorMsg);
        }

    }

}
