package com.mongodb.domain;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocalCMKService {

    private static final String CUSTOMER_KEY_PATH = "src/main/resources/my-key.txt";
    private static final int KEY_SIZE = 96;

    private boolean isCustomerMasterKeyFileExists() {
        return new File(CUSTOMER_KEY_PATH).isFile();
    }

    private void create() throws IOException {
        byte[] cmk = new byte[KEY_SIZE];
        new SecureRandom().nextBytes(cmk);

        try (FileOutputStream stream = new FileOutputStream(CUSTOMER_KEY_PATH)) {
            stream.write(cmk);
        } catch (IOException e) {
            throw new IOException("Unable to write Customer Master Key file: " + e.getMessage(), e);
        }
    }

   private byte[] read() throws IOException {
        byte[] cmk = new byte[KEY_SIZE];

        try (FileInputStream fis = new FileInputStream(CUSTOMER_KEY_PATH)) {
            int bytesRead = fis.read(cmk);
            if (bytesRead != KEY_SIZE) {
                throw new IOException("Expected the customer master key file to be " + KEY_SIZE + " bytes, but read " + bytesRead + " bytes.");
            }
        } catch (IOException e) {
            throw new IOException("Unable to read the Customer Master Key: " + e.getMessage(), e);
        }

        return cmk;
    }

   public Map<String, Map<String, Object>> getKmsProviderCredentials() throws IOException {

        try {
            if (!isCustomerMasterKeyFileExists()) {
                create();
            }

            byte[] localCustomerMasterKey = read();

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("key", localCustomerMasterKey);

            Map<String, Map<String, Object>> kmsProviderCredentials = new HashMap<>();
            kmsProviderCredentials.put("local", keyMap);

            return kmsProviderCredentials;
        }catch (Exception e) {
            throw new IOException("Unable to read the Customer Master Key file: " + e.getMessage(), e);
        }

    }

}
