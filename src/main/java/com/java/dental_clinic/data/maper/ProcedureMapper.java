package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.ProcedureShowDTO;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TreatmentMapper.class})
public interface ProcedureMapper {
    TherapyProcedure toEntity(ProcedureCreationDTO procedureCreationDTO);

    @Mapping(target = "treatmentDTO", source = "treatment")
    ProcedureShowDTO toDTOShow(TherapyProcedure therapyProcedure);
}
