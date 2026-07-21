package com.mongodb.bankaccount.resources.config;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.mongodb")
public class EncryptionConfig {

    private String uri;
    private String kmsProviderName;
    private String keyVaultNamespace;
    private String encryptedDatabaseName;
    private String encryptedCollectionName;
    private String cryptSharedLibPath;

    private static final String CUSTOMER_KEY_PATH = "src/main/resources/customer-key.txt";
    private static final int KEY_SIZE = 96;
    private final Map<String, Map<String, Object>> kmsProviderCredentials = new HashMap<>();

    @Bean
    public ApplicationRunner createEncryptedCollectionRunner(MongoDatabase db, EncryptionFieldConfig encryptionFieldConfig) {
        return args -> {
            if (!encryptionFieldConfig.collectionExists(db, encryptedCollectionName)) {
                encryptionFieldConfig.createEncryptedCollection(db, clientEncryptionSettings());
            }
        };
    }

    private ClientEncryptionSettings clientEncryptionSettings() throws Exception {
        return ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(getMongoClientSettings())
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviderCredentials)
                .build();
    }

    protected MongoClientSettings getMongoClientSettings() throws Exception {
        kmsProviderCredentials.put("local", createLocalKmsProvider());

        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviderCredentials)
                .extraOptions(createExtraOptions())
                .build();

        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .autoEncryptionSettings(autoEncryptionSettings)
                .build();
    }

    private Map<String, Object> createLocalKmsProvider() throws IOException {
        if (!isCustomerMasterKeyFileExists()) {
            generateCustomerMasterKey();
        }

        byte[] localCustomerMasterKey = readCustomerMasterKey();

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", localCustomerMasterKey);

        return keyMap;
    }

    private boolean isCustomerMasterKeyFileExists() {
        return new File(CUSTOMER_KEY_PATH).isFile();
    }
    private void generateCustomerMasterKey() throws IOException {
        byte[] localCustomerMasterKey = new byte[KEY_SIZE];
        new SecureRandom().nextBytes(localCustomerMasterKey);

        try (FileOutputStream stream = new FileOutputStream(CUSTOMER_KEY_PATH)) {
            stream.write(localCustomerMasterKey);
        } catch (IOException e) {
            throw new IOException("Unable to write Customer Master Key file: " + e.getMessage(), e);
        }
    }

    private byte[] readCustomerMasterKey() throws IOException {
        byte[] localCustomerMasterKey = new byte[KEY_SIZE];

        try (FileInputStream fis = new FileInputStream(CUSTOMER_KEY_PATH)) {
            int bytesRead = fis.read(localCustomerMasterKey);
            if (bytesRead != KEY_SIZE) {
                throw new IOException("Expected the customer master key file to be " + KEY_SIZE + " bytes, but read " + bytesRead + " bytes.");
            }
        } catch (IOException e) {
            throw new IOException("Unable to read the Customer Master Key: " + e.getMessage(), e);
        }

        return localCustomerMasterKey;
    }

    private Map<String, Object> createExtraOptions() {
        Map<String, Object> extraOptions = new HashMap<>();
        extraOptions.put("cryptSharedLibPath", cryptSharedLibPath);
        return extraOptions;
    }
}
