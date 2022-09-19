package com.sysco.cdp.service;

import com.sysco.cdp.beans.BulkJobDTO;

public interface AuditJobService {

    /**
     * Function for auditing the job for various positive and negative events
     *
     * @param job
     */
    void logAuditJob(BulkJobDTO job);

    /**
     * Function to audit failure
     *
     * @param jobDTO
     */
    public void auditFailureResponse(BulkJobDTO jobDTO);

    /**
     * Function for auditing the job for various positive and negative events
     *
     * @param jobDTO
     */
    void saveAudit(BulkJobDTO jobDTO);

}
