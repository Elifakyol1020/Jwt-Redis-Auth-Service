package com.redis.redissessionmanagement.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, String cause) {
        super(message + " | Neden: " + cause);
    }
}