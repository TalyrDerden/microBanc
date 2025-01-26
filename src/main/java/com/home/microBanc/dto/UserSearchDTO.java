package com.home.microBanc.dto;

import lombok.Data;

@Data
public class UserSearchDTO extends UserCoreDTO {
    private int page = 0;
    private int size = 10;
}
