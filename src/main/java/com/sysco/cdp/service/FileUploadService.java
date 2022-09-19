package com.sysco.cdp.service;

import com.sysco.cdp.common.exception.FileUploadException;

import java.io.IOException;

public interface FileUploadService {

    void upload(String bucketName, String bucketPath, String fileName, String sourceFileLocalPath) throws IOException, FileUploadException;

}
