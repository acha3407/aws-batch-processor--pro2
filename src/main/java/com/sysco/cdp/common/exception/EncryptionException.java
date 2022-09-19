package com.sysco.cdp.common.exception;

public class EncryptionException extends Exception {

    /**
     * default constructor
     */
    public EncryptionException() {
    }

    /**
     * Intiation with exception message
     *
     * @param message
     */
    public EncryptionException(String message, Object... args) {
        super(String.format(message, args));
    }

}
