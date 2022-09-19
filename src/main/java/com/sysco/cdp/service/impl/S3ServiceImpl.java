package com.sysco.cdp.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.sysco.cdp.common.Constants;
import com.sysco.cdp.common.exception.S3Exception;
import com.sysco.cdp.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service("s3ServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    /**
     * get file from source s3 bucket
     *
     * @param objectKey
     */
    @Override
    public S3Object getS3Object(String bucketName, String objectKey) throws S3Exception {
        // copy object from source bucket folder to the destination bucket folder
        return s3Client.getObject(bucketName, objectKey);
    }

    /**
     * This function will create a subfolder in the S3 bucket
     *
     * @param bucketName
     * @param folderName
     */
    @Override
    public void createFolder(String bucketName, String folderName) {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + Constants.SUFFIX,
                emptyContent, metadata);
        // send request to S3 to create folder
        s3Client.putObject(putObjectRequest);
    }

    /**
     * This function wil return if the folder exists based on the buket name and key
     *
     * @param key
     * @return boolean
     */
    public boolean isFolderExists(String bucketName, String key) {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, key);
        // return the boolean based on comparing if the count exists
        return result.getKeyCount() > 0;
    }

    /**
     * This function is responsible for uploading the file to destinated S3 bucket
     *
     * @param inputFilePath
     * @param s3FileKeyName
     * @throws IOException
     */
    public void uploadToS3(String bucket, String s3FileKeyName, String inputFilePath) throws IOException {
        File inputFile;
        TransferManager transferManager = TransferManagerBuilder.defaultTransferManager();
        try {
            inputFile = new File(inputFilePath);
            // Upload the file to the destination bucket
            Upload upload = transferManager.upload(bucket, s3FileKeyName, inputFile);
            upload.waitForCompletion();
        } catch (Exception ex) {
            log.error("Exception in uploading file to AWS S3 bucket: ", ex);
            Thread.currentThread().interrupt();
            throw new IOException(ex);
        } finally {
            transferManager.shutdownNow();
        }
    }
}
