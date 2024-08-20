package com.java.dental_clinic.data.dto.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffDTO {
    private Long id;
    private String name;
    private String professionalQualification;
    private String phoneNumber;
    private PositionDTO positionDTO;
}
