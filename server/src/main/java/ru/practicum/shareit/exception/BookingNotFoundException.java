package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long bookingId) {
        super(String.format("Бронирование с id = %s не найдено!", bookingId));
    }
}
