package ru.practicum.shareit.exception;

public class EndBeforeStartException extends RuntimeException {
    public EndBeforeStartException() {
        super("Дата окончания не может быть раньше даты старта!");
    }
}
