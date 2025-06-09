package ru.yandex.practicum.exception;

public class OrderBookingNotFoundException extends RuntimeException {
    public OrderBookingNotFoundException(String message) {
        super(message);
    }
}
