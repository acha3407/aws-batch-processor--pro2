package com.sysco.cdp.service.impl;

import com.sysco.cdp.beans.BulkJobDTO;
import com.sysco.cdp.common.Constants;
import com.sysco.cdp.common.exception.EncryptionException;
import com.sysco.cdp.common.exception.FileUploadException;
import com.sysco.cdp.service.ApiService;
import com.sysco.cdp.service.BulkApiAdapterService;
import com.sysco.cdp.service.EncryptionService;
import com.sysco.cdp.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

import static com.sysco.cdp.common.Constants.*;
import static com.sysco.cdp.common.Utility.deleteFile;
import static com.sysco.cdp.common.Utility.saveContentToTempZipFile;

@Service("bulkApiAdapterServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class BulkApiAdapterServiceImpl implements BulkApiAdapterService {

    private final ApiService apiService;

    private final EncryptionService encryptionService;

    private final FileUploadService fileUploadService;


    @Override
    public void loadBulkData(BulkJobDTO dto) throws IOException, EncryptionException, FileUploadException {

        String[] response = apiService.invokeApi(dto, Constants.JobType.valueOf(dto.getJobType().toUpperCase()).checkNextPage());

        String data = response[0];
        String contentType = response[1];
        String error = response[2];

        if (error != null) {
            handleError(error);
        } else if (data != null) {
            handleApiResponse(data, contentType, dto);
        }


    }

    protected void handleApiResponse(String data, String contentType, BulkJobDTO dto) throws IOException, EncryptionException, FileUploadException {
        log.info("handleApiResponse| JobId : {}, response content type : {}", dto.getJobId(), contentType);

        String tempDownloadPath = getTempDownloadPath(dto);
        saveContentToTempZipFile(data, tempDownloadPath);

        String encryptedFilePath = getEncryptedFilePath(dto);
        if (dto.isAmperityEncryption()) {
            /*
             * encrypt file using amperity public key
             * upload encrypted file to amperity s3 bucket
             */
            encryptAndUpload(
                    Constants.EncryptionType.AMPERITY.getPublicKeyBucket(),
                    Constants.EncryptionType.AMPERITY.getPublicKeyPath(),
                    tempDownloadPath,
                    encryptedFilePath,
                    UploadType.AMPERITY.getUploadBucketName(),
                    generateUploadFilePath(dto.getJobType()),
                    generateUploadFileName(dto.getJobType(), true)
            );
            // delete amperity encrypted files
            deleteFile(encryptedFilePath);

        } else {
            //upload downloaded file to amperity s3 bucket without encryption
            fileUploadService.upload(
                    UploadType.AMPERITY.getUploadBucketName(),
                    generateUploadFilePath(dto.getJobType()),
                    generateUploadFileName(dto.getJobType(), false),
                    tempDownloadPath
            );
        }

        if (dto.isSyscoEncryption()) {
            /*
             * encrypt file using sysco public key
             * upload encrypted file to sysco s3 bucket
             */
            encryptAndUpload(
                    EncryptionType.SYSCO.getPublicKeyBucket(),
                    EncryptionType.SYSCO.getPublicKeyPath(),
                    tempDownloadPath,
                    encryptedFilePath,
                    UploadType.SYSCO.getUploadBucketName(),
                    generateUploadFilePath(dto.getJobType()),
                    generateUploadFileName(dto.getJobType(), true)
            );
            // delete sysco encrypted files
            deleteFile(encryptedFilePath);
        }

        //delete downloaded file
        deleteFile(tempDownloadPath);

    }

    private void encryptAndUpload(String publicKeyBucket, String publicKeyBucketPath, String tempDownloadPath,
                                  String encryptedFilePath, String uploadBucketName, String uploadS3Path,
                                  String uploadingFileName) throws EncryptionException, IOException, FileUploadException {
        encryptionService.encrypt(publicKeyBucket, publicKeyBucketPath, tempDownloadPath, encryptedFilePath);

        fileUploadService.upload(uploadBucketName, uploadS3Path, uploadingFileName, encryptedFilePath);
    }

    protected String getTempDownloadPath(BulkJobDTO dto) {
        return Constants.LAMBDA_TMP_FOLDER + dto.getJobId() + GZ;
    }

    protected String getEncryptedFilePath(BulkJobDTO dto) {
        return Constants.LAMBDA_TMP_FOLDER + dto.getJobId() + GZ + Constants.GPG;
    }

    protected String generateUploadFileName(String jobType, boolean encryptionEnabled) {
        LocalDate today = LocalDate.now();
        StringBuilder filename = new StringBuilder(jobType).append("_")
                .append(today.getYear()).append(today.getMonthValue()).append(today.getDayOfMonth())
                .append(GZ);
        if (encryptionEnabled) {
            filename.append(GPG);
        }
        return filename.toString();

    }

    protected String generateUploadFilePath(String jobType) {
        LocalDate today = LocalDate.now();
        StringBuilder filePath = new StringBuilder(jobType).append(SUFFIX)
                .append(today.getYear()).append(SUFFIX)
                .append(today.getMonthValue()).append(SUFFIX)
                .append(today.getDayOfMonth());
        return filePath.toString();

    }

    private void handleError(String error) {
        /**
         * @ TODO: 7/15/2022
         * handle error logic here
         * audits
         */
    }
}
