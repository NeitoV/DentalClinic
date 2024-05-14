package com.java.dental_clinic.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)", nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int cost;

    @Column(name = "unit", columnDefinition = "NVARCHAR(100)")
    private String unit;

    @Column(name = "note", columnDefinition = "NVARCHAR(100)")
    private String note;
}
