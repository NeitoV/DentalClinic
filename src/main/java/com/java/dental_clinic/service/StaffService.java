package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.PaginationDTO;
import com.java.dental_clinic.data.dto.staff.StaffCreationDTO;
import com.java.dental_clinic.data.dto.staff.StaffDTO;
import com.java.dental_clinic.data.entity.Staff;

public interface StaffService {
    MessageResponse createStaff(StaffCreationDTO staffCreationDTO);

    MessageResponse updateStaff(StaffDTO staffDTO, Long id);

    PaginationDTO filterStaff(String keyword, int pageNumber, int pageSize, Long positionId);

    Staff getStaffByToken();
}
