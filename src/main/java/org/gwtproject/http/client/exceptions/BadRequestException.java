package org.gwtproject.http.client.exceptions;

import org.gwtproject.http.client.HttpRequestException;

public class BadRequestException extends HttpRequestException {
    public BadRequestException(String message, int code) {
        super(message, code);
    }
    public BadRequestException() {
    }
}
