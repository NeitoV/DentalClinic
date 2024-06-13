package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.TreatmentDTO;
import com.java.dental_clinic.data.entity.Treatment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ServiceMapper.class})
public interface TreatmentMapper {
    @Mapping(target = "serviceDTO", source = "service")
    TreatmentDTO toDTO(Treatment treatment);
}
