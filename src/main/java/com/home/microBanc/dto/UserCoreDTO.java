package com.home.microBanc.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCoreDTO {
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    private String phone;
}
