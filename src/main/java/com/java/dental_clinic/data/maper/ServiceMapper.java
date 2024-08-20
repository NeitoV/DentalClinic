package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.ServiceDTO;
import com.java.dental_clinic.data.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    ServiceDTO toDTO(Service service);

    @Mapping(target = "id", ignore = true)
    Service toEntity(ServiceDTO serviceDTO);
}
