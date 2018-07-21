package it.alex.transfer.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest implements Serializable {
    @NotEmpty
    private final String transactionId;

    @NotNull
    @Min(1)
    private final Long fromAccountId;

    @NotNull
    @Min(1)
    private final Long toAccountId;

    @NotNull
    private final BigDecimal value;

    @NotEmpty
    @Size(max = 80)
    private final String description;


}
