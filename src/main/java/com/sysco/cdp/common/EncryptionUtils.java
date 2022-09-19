package com.sysco.cdp.common;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sysco.cdp.common.exception.EncryptionException;
import com.sysco.cdp.common.exception.S3Exception;
import com.sysco.cdp.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import static com.sysco.cdp.common.Constants.LAMBDA_TMP_FOLDER;

@Component("encryptionUtils")
@Slf4j
@RequiredArgsConstructor
public class EncryptionUtils {

    private final S3Service s3Service;

    protected void initializeEncryptionKey(String publicKeyBucket, String publicKeyBucketPath) throws EncryptionException {

        try {

            String encryptionFileName = LAMBDA_TMP_FOLDER + publicKeyBucketPath;

            S3Object s3object;
            S3ObjectInputStream inputStream;

            File inputFile = new File(encryptionFileName);
            if (inputFile.exists()) {
                log.debug("PEM file already exist in Lambda temp folder.");
            } else {
                // get encryption file from S3 bucket
                s3object = s3Service.getS3Object(publicKeyBucket, publicKeyBucketPath);
                // Save file object to file
                inputStream = s3object.getObjectContent();
                // copying encrypted object
                FileUtils.copyInputStreamToFile(inputStream, inputFile);
                // closing stream
                inputStream.close();
            }
        } catch (S3Exception | IOException e) {
            log.error("EncryptionUtils | initializeEncryptionKey {} : {}", ErrorConstants.ERROR_ENCRYPTION_OBJECT, e);
            throw new EncryptionException("%s : %s", ErrorConstants.ERROR_ENCRYPTION_OBJECT, e.getMessage());
        }
    }


    public void encryptFile(String publicKeyBucket, String publicKeyPath, String sourceFileLocalPath,
                            String outputLocalFilePath) throws EncryptionException {

        log.info("EncryptionUtils | encryptFile start downloading public key from {}/{}", publicKeyBucket, publicKeyPath);
        initializeEncryptionKey(publicKeyBucket, publicKeyPath);
        log.info("EncryptionUtils | encryptFile complete downloading public key from {}/{}", publicKeyBucket, publicKeyPath);

        PGPPublicKey pubKey;

        String pemFileLocalPath = LAMBDA_TMP_FOLDER + publicKeyPath;

        try (FileInputStream fis = new FileInputStream(pemFileLocalPath)) {
            // get encryption key from lambda temp folder
            pubKey = readPublicKeyFromCol(fis);

            log.debug("EncryptionUtils | encryptFile start encryption source : {}, output {}",
                    sourceFileLocalPath, outputLocalFilePath);

            // call method to get encrypted file
            try (FileOutputStream fos = new FileOutputStream(outputLocalFilePath)) {
                encrypt(fos, sourceFileLocalPath, pubKey);
                log.debug("EncryptionUtils | encryptFile complete encryption source : {}, output {}",
                        sourceFileLocalPath, outputLocalFilePath);
            }
        } catch (IOException | PGPException e) {
            // Log error in case if there are exception during encryption
            log.error(ErrorConstants.ERROR_ENCRYPTION_OBJECT + " : ", e);
            throw new EncryptionException(ErrorConstants.ERROR_ENCRYPTION_OBJECT + e.getMessage());
        }
    }

    public PGPPublicKey readPublicKeyFromCol(InputStream in) throws IOException, PGPException {
        in = PGPUtil.getDecoderStream(in);
        // getting PGP ring collection
        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in,
                new BcKeyFingerprintCalculator());
        PGPPublicKey key = null;
        // iterating key rings
        Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();
        while (key == null && rIt.hasNext()) {
            PGPPublicKeyRing kRing = rIt.next();
            // getting iterator to read through keys
            Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
            // looping through all keys
            while (key == null && kIt.hasNext()) {
                PGPPublicKey k = kIt.next();
                // check if encryption key present
                if (k.isEncryptionKey()) {
                    key = k;
                }
            }
        }
        // if key is null encryption not possible
        if (key == null) {
            throw new IllegalArgumentException("Can't find encryption key in key ring.");
        }

        return key;
    }

    protected void encrypt(OutputStream out, String fileName, PGPPublicKey encKey)
            throws IOException, PGPException {
        // adding provider
        Security.addProvider(new BouncyCastleProvider());
        PGPCompressedDataGenerator comData = null;
        byte[] bytes = null;
        File file = null;
        // try with stream
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
            file = new File(fileName);
            comData = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
            PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, file);
            // getting PGP encryptd data generator
            comData.close();
            PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
                    new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.TRIPLE_DES)
                            .setSecureRandom(new SecureRandom()));
            cPk.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(encKey));
            bytes = bOut.toByteArray();
            try (OutputStream cOut = cPk.open(out, bytes.length)) {
                cOut.write(bytes);
            }
        } finally {
            // closing any open resources
            IOUtils.closeQuietly(out);
        }

    }
}
