package ru.yandex.practicum.filmorate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationException extends RuntimeException {
    ValidationException(final String message) {
        super(message);
    }
}