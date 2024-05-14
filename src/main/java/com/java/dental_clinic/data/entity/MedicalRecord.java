package com.java.dental_clinic.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate examinationDate;

    private LocalDate reExaminationDate;

    @Column(name = "diagnosis", columnDefinition = "NVARCHAR(250)", nullable = false)
    private String diagnosis;

    @Column(name = "note", columnDefinition = "NVARCHAR(250)")
    private String note;
}
