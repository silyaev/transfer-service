package it.alex.transfer.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Builder
@Data
public class TransferResponse implements Serializable {

    private final String id;
    private final Long referenceId;
    private final TransferStatus status;
    private final String description;
    private final TransferRequest request;

}
