package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.LoginDTO;
import com.java.dental_clinic.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phoneNumber", source = "userName")
    User toEntity(LoginDTO loginDTO);
}
