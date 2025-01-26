package com.home.microBanc.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "EMAIL_DATA")
@Data
public class EmailData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Users user;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

}