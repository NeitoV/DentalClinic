package com.java.dental_clinic.data.dto;

import com.java.dental_clinic.data.dto.staff.StaffDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordShowDTO {
    private Long id;
    private String diagnosis;
    private LocalDate examinationDate;
    private StaffDTO staffDTO;
}
