package com.sysco.cdp.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.cdp.beans.BulkJobDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class Utility {

    public static <T> T jsonToObject(String jsonStr, Class<T> classType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(jsonStr, classType);
    }

    public static void saveContentToTempZipFile(String content, String filePath) throws IOException {
        log.info("saveContentToTempZipFile | Save into temp file to path : {}", filePath);
        try (InputStream is = new ByteArrayInputStream(content.getBytes());
             GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(filePath))) {
            byte[] dataBuffer = new byte[Constants.BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(dataBuffer, 0, Constants.BLOCK_SIZE)) != -1) {
                gos.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    /**
     * Function to delete source file
     *
     * @param inputFilePath
     */
    public static boolean deleteFile(String inputFilePath) {
        // after uploading delete the file quietly
        File inputFile = new File(inputFilePath);
        boolean deleteflag = FileUtils.deleteQuietly(inputFile);
        log.debug(
                deleteflag + " : File Delete flag for file download after encryption" + inputFilePath);
        return deleteflag;
    }

    /**
     * this is a common method for error audit
     * @param jobDTO
     * @param errorMsg
     */
    public static void errorAudit(BulkJobDTO jobDTO, String errorMsg) {
        // set status as JobError
        jobDTO.setJobStatus(Constants.JobStatus.JOB_ERROR.getStatus());
        // set error message for audit
        jobDTO.setJobError(errorMsg + jobDTO.getJobType());
    }
}
