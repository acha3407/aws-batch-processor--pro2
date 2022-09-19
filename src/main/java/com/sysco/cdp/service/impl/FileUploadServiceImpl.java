package com.sysco.cdp.service.impl;

import com.sysco.cdp.common.Constants;
import com.sysco.cdp.common.exception.FileUploadException;
import com.sysco.cdp.service.FileUploadService;
import com.sysco.cdp.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static com.sysco.cdp.common.Constants.SUFFIX;
import static com.sysco.cdp.common.ErrorConstants.ENVIRONMNET_NOT_CONFIGURED;

@Service("fileUploadServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final S3Service s3Service;

    private final Environment environment;


    @Override
    public void upload(String bucketName, String bucketPath, String fileName, String sourceFileLocalPath) throws IOException, FileUploadException {

        bucketName = getEnvironment(bucketName);

        log.info("FileUploadServiceImpl | upload: start creating folders in bucket {} folders {}", bucketName, bucketPath);
        checkAndCreateFolders(bucketName, bucketPath.split(SUFFIX));
        log.info("FileUploadServiceImpl | upload: complete creating folders in bucket {}", bucketName);

        getFileSize(sourceFileLocalPath);

        log.info("FileUploadServiceImpl | upload: start uploading file {} to bucket {}, path {} "
                , sourceFileLocalPath, bucketName, bucketPath + SUFFIX + fileName);
        s3Service.uploadToS3(bucketName, bucketPath + SUFFIX + fileName, sourceFileLocalPath);
        log.info("FileUploadServiceImpl | upload: complete uploading files");
    }

    protected void checkAndCreateFolders(String bucket, String... subFolders) {

        StringBuilder sb = new StringBuilder(subFolders[0]);

        for (int i = 1; i < subFolders.length; i++) {
            if (!s3Service.isFolderExists(bucket, sb.toString())) {
                s3Service.createFolder(bucket, sb.toString());
            }
            sb.append(SUFFIX).append(subFolders[i]);
        }
    }

    protected long getFileSize(String filepath) throws IOException {
        // Gather the zip size for logging
        Path filePath = Paths.get(filepath);
        long fileLength = Files.size(filePath);
        log.info(
                "{\"service\":\"{}\", \"process\":\"{}\",\"file\":\"{}\",\"jobdate\":\"{}\", "
                        + "\"jobtime\":\"{}\", \"archivesize\":{}}",
                "cdp", "uploadfilesize", filePath, LocalDate.now(), LocalTime.now(),
                (double) fileLength / Constants.BLOCK_SIZE / Constants.BLOCK_SIZE);
        return fileLength;
    }

    protected String getEnvironment(String key) throws FileUploadException {
        return Optional.ofNullable(environment.getProperty(key))
                .orElseThrow(() -> new FileUploadException("FileUploadServiceImpl | getEnvironment %s for variable %s", ENVIRONMNET_NOT_CONFIGURED, key));
    }


}
