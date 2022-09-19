package com.sysco.cdp.common.exception;

public class FileUploadException extends Exception {

    /**
     * default constructor
     */
    public FileUploadException() {
    }

    /**
     * Intiation with exception message
     *
     * @param message
     */
    public FileUploadException(String message, Object... args) {
        super(String.format(message, args));
    }
}
