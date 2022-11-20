package ru.practicum.shareit.booking.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.Optional;

@Component("InMemoryBookingStorage")
@Slf4j
public class InMemoryBookingStorage implements BookingStorage {
    @Override
    public Booking save(Booking booking) {
        return null;
    }

    @Override
    public void deleteById(Long bookingId) {

    }

    @Override
    public Collection<Booking> findAll() {
        return null;
    }

    @Override
    public void update(Booking booking) {

    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return Optional.empty();
    }
}
