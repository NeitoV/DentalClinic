package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private String userName;
    private String role;
    public JwtResponseDTO(String accessToken, String username, String roles) {
        this.token = accessToken;
        this.userName = username;
        this.role = roles;
    }
}
