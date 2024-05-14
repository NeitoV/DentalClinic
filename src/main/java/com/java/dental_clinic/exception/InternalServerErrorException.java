package com.java.dental_clinic.exception;

public class InternalServerErrorException extends ExceptionCustom {

    public InternalServerErrorException(Object errors) {
        super("UNEXPECTED ERROR OCCURRED", errors);
    }

}
