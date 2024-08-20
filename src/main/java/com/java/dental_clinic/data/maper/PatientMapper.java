package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.patient.PatientCreationDTO;
import com.java.dental_clinic.data.dto.patient.PatientDTO;
import com.java.dental_clinic.data.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "id", ignore = true)
    Patient toEntity(PatientDTO patientDTO);

    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    PatientDTO toDTO(Patient patient);
}
