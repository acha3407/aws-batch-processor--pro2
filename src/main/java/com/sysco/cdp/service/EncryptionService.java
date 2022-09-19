package com.sysco.cdp.service;

import com.sysco.cdp.common.exception.EncryptionException;

public interface EncryptionService {

    void encrypt(String publicKeyBucket, String publicKeyPath, String sourceFileLocalPath, String outputLocalFilePath) throws EncryptionException;

}
