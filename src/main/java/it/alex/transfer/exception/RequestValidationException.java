package it.alex.transfer.exception;

import it.alex.transfer.model.ErrorResponse;
import lombok.Getter;

@Getter
public class RequestValidationException extends RuntimeException {
    private ErrorResponse response;

    public RequestValidationException(String message, ErrorResponse response) {
        super(message);
        this.response = response;
    }
}
