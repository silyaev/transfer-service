package it.alex.transfer.entity;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ACCOUNT_BALANCE")
public class AccountBalanceEntity implements Serializable {
    static final long serialVersionUID = 10L;

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "VALUE", precision = 19, scale = 4)
    private BigDecimal value;

    @Column(name = "LAST_UPDATE")
    @UpdateTimestamp
    private LocalDateTime lastUpdated;

}
