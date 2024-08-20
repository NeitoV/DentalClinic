package com.java.dental_clinic.data.dto;

import com.java.dental_clinic.data.dto.staff.StaffDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DentistRevenueDTO {
    private StaffDTO staffDTO;
    private List<Map<String, Object>> revenues;
}
