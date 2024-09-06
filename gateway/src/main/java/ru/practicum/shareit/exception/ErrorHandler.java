package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleArgumentNotValidException(final MethodArgumentNotValidException ex) {
        return Map.of("error", ex.getBindingResult().getFieldErrors().stream().map(fe -> "Validation error: " + fe.getDefaultMessage() +
                " -> field: " + fe.getField()).toList());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(final ConstraintViolationException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }
}
