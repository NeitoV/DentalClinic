package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.RoleDTO;
import com.java.dental_clinic.data.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
}
