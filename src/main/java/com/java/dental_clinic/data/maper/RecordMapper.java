package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.RecordCreationDTO;
import com.java.dental_clinic.data.dto.RecordShowDTO;
import com.java.dental_clinic.data.entity.MedicalRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StaffMapper.class})
public interface RecordMapper {
    MedicalRecord toEntity(RecordCreationDTO recordCreationDTO);

    @Mapping(target = "staffDTO", source = "staff")
    RecordShowDTO toDTOShow(MedicalRecord medicalRecord);
}
