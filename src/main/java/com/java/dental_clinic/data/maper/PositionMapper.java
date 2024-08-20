package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.staff.PositionDTO;
import com.java.dental_clinic.data.entity.Position;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    PositionDTO toDTO(Position position);
}
