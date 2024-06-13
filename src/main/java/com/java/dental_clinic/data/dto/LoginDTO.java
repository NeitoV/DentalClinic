package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDTO {
    @NotNull(message = "phone number khong duoc de trong")
    private String userName;

    @NotNull(message = "password khong duoc de trong")
    private String password;
}
