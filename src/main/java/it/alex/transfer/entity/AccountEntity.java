package it.alex.transfer.entity;

import lombok.*;

import javax.persistence.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity extends AbstractEntity {
    static final long serialVersionUID = 10L;

    @Column(name = "NUMBER", columnDefinition = "char")
    private String number;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

}
