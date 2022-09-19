package com.sysco.cdp.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkJobDTO implements Serializable {


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1295835946660705407L;
    /**
     * jobType
     */
    private String jobType;

    /**
     * JobError
     */
    private String jobError;

    /**
     * jobStatus
     */
    private String jobStatus;

    /**
     * jobId
     */
    private String jobId;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestHeader {
        private String Key;
        private String value;
    }

    /**
     * apiUrl
     */
    private String apiUrl;
    /**
     * requestType
     */
    private String requestType;

    /**
     * Timebase
     */
    private String timeBase;
    /**
     * requestHeaders
     */
    private List<RequestHeader> requestHeaders;
    /**
     * requestBody
     */
    private String requestBody;

    /**
     * AmperityEncryption
     */
    @Builder.Default
    private boolean amperityEncryption = true;

    /**
     * SyscoEncryption
     */
    @Builder.Default
    private boolean syscoEncryption = true;


}
