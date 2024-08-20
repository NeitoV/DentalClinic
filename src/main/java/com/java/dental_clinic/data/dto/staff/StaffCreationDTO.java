package com.java.dental_clinic.data.dto.staff;

import com.java.dental_clinic.data.dto.LoginDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffCreationDTO {
    private StaffDTO staffDTO;
    private LoginDTO loginDTO;
    private Long positionId;
}
