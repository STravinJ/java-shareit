package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long itemId) {
        super(String.format("Вещь с id = %s не найдена!", itemId));
    }
}
