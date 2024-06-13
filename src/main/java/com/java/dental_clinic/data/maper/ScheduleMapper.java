package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.ScheduleDTO;
import com.java.dental_clinic.data.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WorkingMapper.class, PatientMapper.class})
public interface ScheduleMapper {
    @Mapping(target = "workingShowDTO", source = "calendarWorking")
    @Mapping(target = "patientDTO", source = "patient")
    ScheduleDTO toDTO(Schedule schedule);
}
