package com.sysco.cdp.common.exception;

public class S3Exception extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8199613202010860432L;

    /**
     * default constructor
     */
    public S3Exception() {
    }

    /**
     * Intiation with exception message
     *
     * @param message
     */
    public S3Exception(String message, Object ...args) {
        super(String.format(message, args));
    }

}

