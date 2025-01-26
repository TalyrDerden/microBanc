package com.home.microBanc.modal;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Entity
@Table(name = "USERS")
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 500)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EmailData> emailData;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PhoneData> phoneData;

}

