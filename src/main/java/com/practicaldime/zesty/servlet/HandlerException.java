package com.practicaldime.zesty.servlet;

public class HandlerException extends RuntimeException {

    public final int status;

    public HandlerException(int status, String message) {
        super(message);
        this.status = status;
    }

    public HandlerException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
