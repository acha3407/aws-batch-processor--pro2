package com.sysco.cdp.common;

public class ErrorConstants {

    private ErrorConstants() {
    }

    /**
     * ENVIRONMNET_NOT_CONFIGURED
     */
    public static final String ENVIRONMNET_NOT_CONFIGURED = "Envrironment Not configured";

    /**
     * NON_STANDARD_RESPONSE
     */
    public static final String NON_STANDARD_RESPONSE = "API Response was not standard : ";

    /**
     * API_INVOCATION_ERROR
     */
    public static final String API_INVOCATION_ERROR = "Error during API invocation ";

    /**
     * STATUS_CHECK_INVOCATION_ERROR
     */
    public static final String STATUS_CHECK_INVOCATION_ERROR = "Error during status check API invocation : ";

    /**
     * ERROR_DURING_AUDIT_LOGGING
     */
    public static final String ERROR_DURING_AUDIT_LOGGING = "Error during the audit log operation : ";

    /**
     * ERROR_DURING_FILE_TRANSFER
     */
    public static final String ERROR_DURING_FILE_TRANSFER = "Error during the File Transfer : ";

    /**
     * ERRPR_DURING_TOKEN_GENERATION
     */
    public static final String ERROR_DURING_TOKEN_GENERATION = "Error during token generation : ";

    /**
     * ERROR_DURING_SECRET_ACCESS
     */
    public static final String ERROR_DURING_SECRET_ACCESS = "Error during secret access : ";

    // constants for tealium
    /**
     *
     * ERROR_DURING_FILE_TRANSFER
     */
    public static final String ERROR_TEALIUM_OBJECT_TRANSFER = "Error during CX-Syndication object transfer : ";

    /**
     *
     * ERROR_TEALIUM_NO_OBJECT_FOUND
     */
    public static final String ERROR_TEALIUM_NO_OBJECT_FOUND = "Error due to no object found in the source folder : ";

    /**
     *
     * ERROR_DURING_FILE_TRANSFER
     */
    public static final String ERROR_TEALIUM_NO_FOLDER_FOUND = "Error due to source folder not found ";

    /**
     *
     * ERROR_DURING_FILE_TRANSFER
     */
    public static final String ERROR_TEALIUM_EMPTY_INPUT_DATE = "Error due to no input date provided";

    // constants for encryption
    /**
     *
     * ERROR_DURING_FILE_TRANSFER
     */
    public static final String ERROR_ENCRYPTION_OBJECT = "Error during Encrypting S3 object";

}

