package it.alex.transfer.entity;

import it.alex.transfer.model.TransferStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "TRANSFER_HISTORY")
public class TransferHistoryEntity extends AbstractEntity {
    static final long serialVersionUID = 10L;

    @Column(name = "FROM_ACCOUNT")
    private long fromAccount;

    @Column(name = "TO_ACCOUNT")
    private long toAccount;

    @Column(name = "VALUE", precision = 19, scale = 4)
    private BigDecimal value;

    @Column(name = "STATUS", columnDefinition = "char")
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column(name = "DESCRIPTION")
    private String description;
}
