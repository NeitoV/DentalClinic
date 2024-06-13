package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.WorkingDTO;
import com.java.dental_clinic.data.dto.WorkingDTOWithID;
import com.java.dental_clinic.data.dto.WorkingShowDTO;
import com.java.dental_clinic.data.entity.CalendarWorking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StaffMapper.class})
public interface WorkingMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "workingDTO.date", source = "date")
    @Mapping(target = "workingDTO.periodId", source = "period.id")
    @Mapping(target = "staffDTO", source = "staff")
    WorkingShowDTO toDTOShow(CalendarWorking calendarWorking);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "workingDTO.date", source = "date")
    @Mapping(target = "workingDTO.periodId", source = "period.id")
    WorkingDTOWithID toDTO(CalendarWorking calendarWorking);
}
