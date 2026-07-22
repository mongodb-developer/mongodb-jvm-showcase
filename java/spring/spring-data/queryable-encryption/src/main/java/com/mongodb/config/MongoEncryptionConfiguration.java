package com.mongodb.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoJsonSchemaCreator;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import com.mongodb.domain.Employee;
import com.mongodb.domain.LocalCMKService;

@Configuration
@Order(1)
public class MongoEncryptionConfiguration implements ApplicationRunner {

    private final AppProperties appProperties;
	private final LocalCMKService localCMKService;

    MongoEncryptionConfiguration(LocalCMKService localCMKService, AppProperties appProperties) {
        this.localCMKService = localCMKService;
        this.appProperties = appProperties;
    }

    @Bean
    public MongoClient mongoClient() throws IOException {
        return MongoClients.create(getMongoClientSettings());
    }

    @Bean
    MongoOperations mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, appProperties.encryptedDatabaseName);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var mongoTemplate = mongoTemplate(mongoClient());

        if (! mongoTemplate.collectionExists(appProperties.encryptedCollectionName)) {
            initializeEncryptedCollection(mongoTemplate);
        }
    }

    private void initializeEncryptedCollection(MongoOperations template) throws IOException {
        try (ClientEncryption clientEncryption = createClientEncryption()) {
            createCollectionFromSchema(template, clientEncryption);
        }
    }

    private ClientEncryption createClientEncryption() throws IOException {
        var encryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(
                        MongoClientSettings.builder()
                                .applyConnectionString(new ConnectionString(appProperties.uri))
                                .build())
                .keyVaultNamespace(appProperties.keyVaultNamespace)
                .kmsProviders(localCMKService.getKmsProviderCredentials())
                .build();

        return ClientEncryptions.create(encryptionSettings);
    }

    private void createCollectionFromSchema(MongoOperations template, ClientEncryption clientEncryption) {
        MongoJsonSchema schema = MongoJsonSchemaCreator.create(new MongoMappingContext())
                .filter(MongoJsonSchemaCreator.encryptedOnly())
                .createSchemaFor(Employee.class);

        Document encryptedFields = CollectionOptions.encryptedCollection(schema)
                .getEncryptedFieldsOptions()
                .map(CollectionOptions.EncryptedFieldsOptions::toDocument)
                .orElseThrow();

        template.execute(db -> clientEncryption.createEncryptedCollection(
                db,
                template.getCollectionName(Employee.class),
                new CreateCollectionOptions().encryptedFields(encryptedFields),
                new CreateEncryptedCollectionParams("local")
        ));
    }

    private MongoClientSettings getMongoClientSettings() throws IOException {
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(appProperties.uri))
                .autoEncryptionSettings(getAutoEncryptionSettings())
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
    }

    private AutoEncryptionSettings getAutoEncryptionSettings() throws IOException {
		Map<String, Map<String, Object>> kmsProviderCredentials = localCMKService.getKmsProviderCredentials();

        return AutoEncryptionSettings.builder()
                .keyVaultNamespace(appProperties.keyVaultNamespace)
                .extraOptions(createExtraOptions())
                .kmsProviders(kmsProviderCredentials)
                .build();
    }

    private Map<String, Object> createExtraOptions() {
        Map<String, Object> extraOptions = new HashMap<>();
        extraOptions.put("cryptSharedLibPath", appProperties.cryptSharedLibPath);
        return extraOptions;
    }
}

// TODO: Alternative way to create the encrypted collection manually
/*
 * The method below shows a manual approach to define the encryptedFields configuration
 * programmatically, using a Data Encryption Key (DEK) generated at runtime.
 *
 * While we use the derived schema approach in this example,
 * this method is useful when you want full control over field-level encryption metadata.
 */
/*
private void manualCollectionSetup(ClientEncryption clientEncryption, MongoOperations mongoTemplate) {
    BsonBinary dkSalary = clientEncryption.createDataKey("local", new DataKeyOptions());

    CollectionOptions collectionOptions = CollectionOptions.encryptedCollection(options -> options
        .queryable(
            encrypted(float64("salary"))
                .algorithm("Range")
                .keyId(dkSalary.asUuid()),
            range().contention(0).precision(2).min(0.0).max(9999999.0)
        )
    );

    mongoTemplate.createCollection(Employee.class, collectionOptions);
}
*/




