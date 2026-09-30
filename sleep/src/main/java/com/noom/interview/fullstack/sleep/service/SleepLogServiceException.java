package com.noom.interview.fullstack.sleep.service;

public class SleepLogServiceException extends Exception {

    public enum Type {
        INVALID_REQUEST,
        NOT_FOUND
    }

    private final Type type;

    public SleepLogServiceException(String message) {
        this(message, Type.INVALID_REQUEST);
    }

    public SleepLogServiceException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
