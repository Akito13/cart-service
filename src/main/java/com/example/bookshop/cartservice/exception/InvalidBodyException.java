package com.example.bookshop.cartservice.exception;

public class InvalidBodyException extends RuntimeException{
    public InvalidBodyException(String message) {
        super(message);
    }
}
