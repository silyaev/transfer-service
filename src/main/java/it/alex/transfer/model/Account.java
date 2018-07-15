package it.alex.transfer.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Account {

    private Long id;
    private String number;
    private String name;
    private String description;
    private BigDecimal balance;
}
