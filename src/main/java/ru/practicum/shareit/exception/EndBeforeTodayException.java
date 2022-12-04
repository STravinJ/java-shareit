package ru.practicum.shareit.exception;

public class EndBeforeTodayException extends RuntimeException {
    public EndBeforeTodayException() {
        super("Дата окончания не может быть раньше текущей даты!");
    }
}
