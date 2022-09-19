package com.sysco.cdp.service;

import com.amazonaws.services.s3.model.S3Object;
import com.sysco.cdp.common.exception.S3Exception;

import java.io.IOException;

public interface S3Service {

    S3Object getS3Object(String bucketName, String objectKey) throws S3Exception;

    void createFolder(String bucketName, String folderName);

    boolean isFolderExists(String bucketName, String key);

    /**
     * @param inputFilePath
     * @param s3FileKeyName
     * @throws IOException
     */
    public void uploadToS3(String bucketName, String s3FileKeyName, String inputFilePath) throws IOException;


}
