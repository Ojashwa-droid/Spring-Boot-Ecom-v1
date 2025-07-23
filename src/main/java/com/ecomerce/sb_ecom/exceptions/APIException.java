package com.ecomerce.sb_ecom.exceptions;

public class APIException extends RuntimeException{
    private static final Long serialVersionUID = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
