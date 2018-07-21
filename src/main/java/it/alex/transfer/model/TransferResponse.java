package it.alex.transfer.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Builder
@Data
public class TransferResponse implements Serializable {

    private final String id;
    private final Long referenceId;
    private final TransferStatus status;
    private BigDecimal balance;
    private final String description;
    private final TransferRequest request;

}
