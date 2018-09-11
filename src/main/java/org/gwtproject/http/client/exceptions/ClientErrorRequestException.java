package org.gwtproject.http.client.exceptions;

import org.gwtproject.http.client.HttpRequestException;

public class ClientErrorRequestException extends HttpRequestException {
    public ClientErrorRequestException(String message, int code) {
        super(message, code);
    }
    public ClientErrorRequestException() {
    }
}
