package org.gwtproject.http.client.exceptions;

import org.gwtproject.http.client.HttpRequestException;

public class NotFoundRequestException extends HttpRequestException {
    public NotFoundRequestException(String message, int code) {
        super(message, code);
    }
    public NotFoundRequestException() {
    }
}
