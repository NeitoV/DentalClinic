package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectiveDTO {
    private Long id;
    private String diagnosis;
    private LocalDate examinationDate;
    private String note;
}
