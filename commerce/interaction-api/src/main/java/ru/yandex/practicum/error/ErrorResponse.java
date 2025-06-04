package ru.yandex.practicum.error;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    Throwable cause;
    StackTraceElement[] stackTrace;
    HttpStatus httpstatus;
    String userMessage;
    String message;
    Throwable[] suppressed;
    String localizedMessage;
}