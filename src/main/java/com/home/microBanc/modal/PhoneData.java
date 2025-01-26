package com.home.microBanc.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "PHONE_DATA")
@Data
public class PhoneData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Users user;

    @Column(nullable = false, length = 13, unique = true)
    private String phone;

}
