package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.ObjectiveDTO;
import com.java.dental_clinic.data.dto.ObjectiveShowDTO;
import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import com.java.dental_clinic.data.entity.Objective;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObjectiveMapper {

    Objective toEntity(ObjectivesCreationDTO objectivesCreationDTO);

    ObjectiveShowDTO toDTOShow(Objective objective);

    ObjectiveDTO toDTO(Objective objective);
}
