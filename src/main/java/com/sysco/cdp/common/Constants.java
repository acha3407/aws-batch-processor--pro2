package com.sysco.cdp.common;

import com.sysco.cdp.paginationHandler.DefaultPageRequest;
import com.sysco.cdp.paginationHandler.OktaPageRequest;
import com.sysco.cdp.paginationHandler.PageRequest;

public class Constants {

    /**
     * Suffix
     */
    public static final String SUFFIX = "/";
    /**
     * Lambda temp folder
     */
    public static final String LAMBDA_TMP_FOLDER = "/tmp/";
    /**
     * Gzip
     */
    public static final String GZ = ".gz";
    /**
     * gpg
     */
    public static final String GPG = ".gpg";
    /**
     * ENCRYPTION_TEMP_FOLDER_NAME
     */
    public static final String ENCRYPTION_TEMP_FOLDER_NAME = "encryption";

    /**
     * BLOCK_SIZE
     */
    public static final int BLOCK_SIZE = 1024;

    /**
     * Enumeration for the source type
     */
    public enum SourceType {

        OKTA("okta");

        private String type;

        SourceType(String type) {
            this.type = type;
        }

        public String getName() {
            return this.type;
        }

    }

    /**
     * Enumeration for all the Job Types
     */
    public enum JobType {

        OKTA("okta") {
            @Override
            public PageRequest checkNextPage() {
                return new OktaPageRequest();
            }

            @Override
            public String getSourceType() {
                return null;
            }

        };

        public static JobType getValue(String value) {
            for (JobType e : JobType.values()) {
                if (e.type.equals(value)) {
                    return e;
                }
            }
            return null;// not found
        }

        private String type;


        JobType(String type) {
            this.type = type;
        }

        public abstract PageRequest checkNextPage();

        public abstract String getSourceType();

    }

    /**
     * Enumeration for all the status
     */
    public enum JobStatus {
        JOB_INITIATED("JobInitiated"), TOKEN_GENERATED("TokenGenerated"), JOBID_GENERATED("JobIdGenerated"),
        JOB_COMPLETED("Completed"), JOB_UPLOAD_COMPLETED("Ingested"), JOB_ERROR("JobError"),
        JOB_REINITIATED("JobReInitiated"), JOB_COMPLETE("JobComplete");

        private String status;

        JobStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

    }

    public static final String AmperityPublicKeyBucket = "cdp.encryption.amperity.publicKeyBucket";

    public static final String AmperityPublicKeyBucketPath = "cdp.encryption.amperity.publicKeyPath";

    public static final String SyscoPublicKeyBucket = "cdp.encryption.sysco.publicKeyBucket";

    public static final String SyscoPublicKeyBucketPath = "cdp.encryption.sysco.publicKeyPath";

    public enum EncryptionType {

        AMPERITY {
            @Override
            public String getPublicKeyBucket() {
                return AmperityPublicKeyBucket;
            }

            @Override
            public String getPublicKeyPath() {
                return AmperityPublicKeyBucketPath;
            }
        },
        SYSCO {
            @Override
            public String getPublicKeyBucket() {
                return SyscoPublicKeyBucket;
            }

            @Override
            public String getPublicKeyPath() {
                return SyscoPublicKeyBucketPath;
            }
        };

        public abstract String getPublicKeyBucket();

        public abstract String getPublicKeyPath();

    }

    public static String amperityDestinationBucket = "cdp.upload.amperity.destinationBucket";

    public static String syscoDestinationBucket = "cdp.upload.sysco.destinationBucket";

    public enum UploadType {

        AMPERITY {
            @Override
            public String getUploadBucketName() {
                return amperityDestinationBucket;
            }
        }, SYSCO {
            @Override
            public String getUploadBucketName() {
                return syscoDestinationBucket;
            }
        };

        public abstract String getUploadBucketName();

    }


    /**
     * Enumeration for all the status
     */
    public enum AuditAttrib {

        ID("id"), JOB_ID("job_id"), JOB_TYPE("job_type"), JOB_STATUS("job_status"),
        JOB_API_URL("job_api_url"), JOB_ERROR("job_error"), TIME_STAMP("timestamp");

        private String name;

        AuditAttrib(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }

    /**
     * AUDIT_DATE_FORMAT
     */
    public static final String AUDIT_DATE_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static final String AUDIT_TABLE = "cdp.audit.tableName";
}