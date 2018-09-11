package org.gwtproject.http.client.exceptions;

import org.gwtproject.http.client.HttpRequestException;

public class UnauthorizedRequestException extends HttpRequestException {
    public UnauthorizedRequestException(String message, int code) {
        super(message, code);
    }
    public UnauthorizedRequestException() {
    }
}
