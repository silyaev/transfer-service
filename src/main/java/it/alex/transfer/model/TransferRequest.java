package it.alex.transfer.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest implements Serializable {
    private final String transactionId;
    private final Long fromAccountId;
    private final Long toAccountId;
    private final BigDecimal value;
    private final String description;


}
