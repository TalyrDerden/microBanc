package com.home.microBanc.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.home.microBanc.contants.ErrorMessages.CANNOT_LESS;

@Entity
@Table(name = "ACCOUNT")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    @JsonIgnore
    @ToString.Exclude
    private Users user;

    @Column(nullable = false, scale = 2)
    private BigDecimal balance;

    @AssertTrue(message = CANNOT_LESS)
    public boolean isBalanceValid() {
        return balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false, scale = 2)
    private BigDecimal startBalance;

    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false)
    private Timestamp startDate;
}
