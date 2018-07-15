package it.alex.transfer.exception;

public class RequestValidationExeption extends RuntimeException {
    public RequestValidationExeption(String message) {
        super(message);
    }

}
