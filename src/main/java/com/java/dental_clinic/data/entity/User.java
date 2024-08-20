package com.java.dental_clinic.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", columnDefinition = "NVARCHAR(250)", unique = true)
    private String email;

    @Column(name = "phone_number", columnDefinition = "NVARCHAR(250)", nullable = false, unique = true)
    private String phoneNumber;

    @JsonIgnore
    @Column(name = "password", columnDefinition = "NVARCHAR(250)", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private String otp;
}
