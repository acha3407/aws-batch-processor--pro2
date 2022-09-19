package com.sysco.cdp.service;

import com.sysco.cdp.beans.BulkJobDTO;
import com.sysco.cdp.common.exception.EncryptionException;
import com.sysco.cdp.common.exception.FileUploadException;

import java.io.IOException;

public interface BulkApiAdapterService {

    void loadBulkData(BulkJobDTO dto) throws IOException, EncryptionException, FileUploadException;

}
