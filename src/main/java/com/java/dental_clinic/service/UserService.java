package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.JwtResponseDTO;
import com.java.dental_clinic.data.dto.LoginDTO;
import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.entity.User;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;

public interface UserService {
    JwtResponseDTO loginUser(LoginDTO loginDTO);

    User createUser(LoginDTO loginDTO, long roleId);

    User getUserByToken();

    MessageResponse sendMailActiveUser(@Email String email) throws MessagingException;

    MessageResponse activeEmail(String email, String otp);
}
