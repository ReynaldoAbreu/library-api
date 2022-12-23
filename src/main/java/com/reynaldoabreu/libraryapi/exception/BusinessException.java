package com.reynaldoabreu.libraryapi.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String messageError) {
        super(messageError);
    }
}
