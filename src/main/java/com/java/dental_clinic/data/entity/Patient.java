package com.java.dental_clinic.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "NVARCHAR(250)", nullable = false)
    private String name;

    @Column(name = "birthday", columnDefinition = "VARCHAR(20)", nullable = false)
    private String birthday;

    @Column(name = "address", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String address;

    private boolean gender;

    @Column(name = "medical_history", columnDefinition = "NVARCHAR(250)")
    private String medicalHistory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
