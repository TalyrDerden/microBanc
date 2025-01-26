package com.home.microBanc.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class UserRequest extends UserCoreDTO {
    private String password;
    private BigDecimal startBalance;
}
