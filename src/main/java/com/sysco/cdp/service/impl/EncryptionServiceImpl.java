package com.sysco.cdp.service.impl;

import com.sysco.cdp.common.EncryptionUtils;
import com.sysco.cdp.common.exception.EncryptionException;
import com.sysco.cdp.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sysco.cdp.common.ErrorConstants.ENVIRONMNET_NOT_CONFIGURED;

@Service("encryptionServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    private final Environment environment;

    private final EncryptionUtils encryptionUtils;

    @Override
    public void encrypt(String publicKeyBucket, String publicKeyPath, String sourceFileLocalPath,
                        String outputLocalFilePath) throws EncryptionException {

        publicKeyBucket = getEnvironment(publicKeyBucket);
        publicKeyPath = getEnvironment(publicKeyPath);

        encryptionUtils.encryptFile(publicKeyBucket, publicKeyPath, sourceFileLocalPath, outputLocalFilePath);

    }

    protected String getEnvironment(String key) throws EncryptionException {
        return Optional.ofNullable(environment.getProperty(key))
                .orElseThrow(() -> new EncryptionException("EncryptionServiceImpl | getEnvironment %s for variable %s"
                        , ENVIRONMNET_NOT_CONFIGURED, key));
    }
}
