package com.librarymanagement.exception;

public class NoCopiesAvailableException extends  RuntimeException {

    public NoCopiesAvailableException(String message) {
        super(message);
    }

}
