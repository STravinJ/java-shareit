package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.Optional;

public interface BookingStorage {

    Booking save(Booking booking);

    void deleteById(Long bookingId);

    Collection<Booking> findAll();

    void update(Booking booking);

    Optional<Booking> findById(Long bookingId);

}
