package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.staff.StaffDTO;
import com.java.dental_clinic.data.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PositionMapper.class})
public interface StaffMapper {
    @Mapping(target = "id", ignore = true)
    Staff toEntity(StaffDTO staffDTO);

    @Mapping(target = "positionDTO", source = "position")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    StaffDTO toDTO(Staff staff);
}
