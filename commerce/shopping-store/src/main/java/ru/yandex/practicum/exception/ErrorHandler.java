package ru.yandex.practicum.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.error.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleFeignException(final FeignException e) {
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e.getMessage(),
                e.getMessage(),
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFoundException(final ProductNotFoundException  e) {
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                e.getMessage(),
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }
}
