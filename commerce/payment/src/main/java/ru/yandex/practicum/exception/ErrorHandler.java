package ru.yandex.practicum.exception;

import feign.FeignException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.error.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NoOrderFoundException.class, ProductNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotEnoughInfoInOrderToCalculateException(final NotEnoughInfoInOrderToCalculateException e) {
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                e.getMessage(),
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

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
}
