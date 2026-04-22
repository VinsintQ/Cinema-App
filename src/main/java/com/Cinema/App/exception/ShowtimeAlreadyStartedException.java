package com.Cinema.App.exception;

public class ShowtimeAlreadyStartedException extends RuntimeException {
    public ShowtimeAlreadyStartedException(String message) {
        super(message);
    }
}
