package ru.practicum.ewm.exception;

public class ValidationConflictException extends RuntimeException {
    public ValidationConflictException(String message) {
        super(message);
    }
}