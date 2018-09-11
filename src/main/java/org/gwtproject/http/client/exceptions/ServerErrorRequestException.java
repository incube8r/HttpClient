package org.gwtproject.http.client.exceptions;

import org.gwtproject.http.client.HttpRequestException;

public class ServerErrorRequestException extends HttpRequestException {
    public ServerErrorRequestException(String message, int code) {
        super(message, code);
    }
    public ServerErrorRequestException() {
    }
}
