package com.sysco.cdp.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.sysco.cdp.beans.BulkJobDTO;

import static com.sysco.cdp.common.Constants.*;
import static com.sysco.cdp.common.Utility.errorAudit;

import com.sysco.cdp.common.ErrorConstants;
import com.sysco.cdp.service.AuditJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

@Service("auditJobServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class AuditJobServiceImpl implements AuditJobService {

    private static final String LOG_PATTERN = "{\"service\":\"{}\", \"process\":\"{}\", \"jobtype\":\"{}\", "
            + "\"jobstatus\":\"{}\", \"jobId\":\"{}\","
            + " \"joberror\":\"{}\", \"jobdate\":\"{}\", \"jobtime\":\"{}\"}";

    private final AmazonDynamoDB dbClient;

    private final Environment environment;

    @Override
    public void logAuditJob(BulkJobDTO job) {

        String SourceType = JobType.valueOf(job.getJobType().toUpperCase()).getSourceType();
        log.info(LOG_PATTERN, "cdp", SourceType, job.getJobType(), job.getJobStatus(), job.getJobId(),
                job.getJobError(), LocalDate.now(), LocalTime.now());
    }

    @Override
    public void auditFailureResponse(BulkJobDTO jobDTO) {
        jobDTO.setJobStatus(JobStatus.JOB_ERROR.getStatus());
        logAuditJob(jobDTO);
    }

    @Override
    public void saveAudit(BulkJobDTO jobDTO) {

        // Populate the JobDTO with all the auditing columns needed for the JOB
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        // Set UUID for primary key
        itemValues.put(AuditAttrib.ID.getName(), new AttributeValue(UUID.randomUUID().toString()));
        // Set job id
        itemValues.put(AuditAttrib.JOB_ID.getName(), new AttributeValue(jobDTO.getJobId()));
        // Set Job Type
        itemValues.put(AuditAttrib.JOB_TYPE.getName(), new AttributeValue(jobDTO.getJobType()));
        // Set SeedJob Id
        itemValues.put(AuditAttrib.JOB_API_URL.getName(), new AttributeValue(jobDTO.getApiUrl()));
        // Set job status
        itemValues.put(AuditAttrib.JOB_STATUS.getName(), new AttributeValue(jobDTO.getJobStatus()));
        // Set job error
        itemValues.put(AuditAttrib.JOB_ERROR.getName(), new AttributeValue(jobDTO.getJobError()));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(AUDIT_DATE_FORMAT);
        String date = dtf.format(LocalDateTime.now());
        // Set audit time stamp
        itemValues.put(AuditAttrib.TIME_STAMP.getName(), new AttributeValue(date));

        try {

            // Save entry in dynamodb
            dbClient.putItem(getAuditTableName(), itemValues);
        } catch (Exception e) {
            log.error(ErrorConstants.ERROR_DURING_AUDIT_LOGGING + jobDTO.getJobId() + " : ", e);
            /*
             * Exception is not thrown back deliberately, since the auditing error should
             * not block the complete process.
             */
            errorAudit(jobDTO, ErrorConstants.ERROR_DURING_AUDIT_LOGGING);
        }

    }

    public String getAuditTableName() {
        return environment.getProperty(AUDIT_TABLE);
    }
}
