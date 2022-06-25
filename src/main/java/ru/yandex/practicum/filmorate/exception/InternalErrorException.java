package ru.yandex.practicum.filmorate.exception;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(final String message) {
        super(message);
    }
}