package ru.practicum.shareit.exception;

public class StartBeforeTodayException extends RuntimeException {
    public StartBeforeTodayException() {
        super("Дата начала не может быть раньше текущей даты!");
    }
}
