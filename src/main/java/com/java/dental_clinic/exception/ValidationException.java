package com.java.dental_clinic.exception;

import java.util.Map;

public class ValidationException extends ExceptionCustom{

    public ValidationException(Map<String, Object> errors) {
        super("DATA INVALID", errors);
    }

}
