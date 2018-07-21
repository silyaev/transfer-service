package it.alex.transfer.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ErrorResponse implements Serializable {
    private final List<String> errors;
    private final String request;
    private final String description;
    private final Integer code;
}
