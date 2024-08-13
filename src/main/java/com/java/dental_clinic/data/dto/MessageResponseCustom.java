package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseCustom extends MessageResponse{
    private Long id;

    public MessageResponseCustom(int httpCode, String message, Long id) {
        super(httpCode, message);
        this.id = id;
    }
}
