package com.mongodb.bankaccount.resources.config;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.*;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class EncryptionFieldConfig {

    int MAX = 999999999;
    int MIN = 0;
    int PRECISION = 2;

    protected boolean collectionExists(MongoDatabase db, String collectionName) {
        return db.listCollectionNames().into(new ArrayList<>()).contains(collectionName);
    }

    protected void createEncryptedCollection(MongoDatabase db, ClientEncryptionSettings clientEncryptionSettings) {
        var clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
        var encryptedCollectionParams = new CreateEncryptedCollectionParams("local")
                .masterKey(new BsonDocument());

        var createCollectionOptions = new CreateCollectionOptions().encryptedFields(encryptFields());

        clientEncryption.createEncryptedCollection(db, "accounts", createCollectionOptions, encryptedCollectionParams);
    }

    private BsonDocument encryptFields() {
        return new BsonDocument().append("fields",
                new BsonArray(Arrays.asList(
                        createEncryptedField("accountNumber", "string", equalityQueryType()),
                        createEncryptedField("cardVerificationCode", "int", equalityQueryType()),
                        createEncryptedField("accountBalance", "double", rangeQueryType()
                        ))));
    }

    private BsonDocument createEncryptedField(String path, String bsonType, BsonDocument query) {

        return new BsonDocument()
                .append("keyId", new BsonNull())
                .append("path", new BsonString(path))
                .append("bsonType", new BsonString(bsonType))
                .append("queries", query);
    }

    private BsonDocument equalityQueryType() {
        return new BsonDocument().append("queryType", new BsonString("equality"));
    }

    private BsonDocument rangeQueryType() {
        return new BsonDocument()
                .append("queryType", new BsonString("range"))
                .append("min", new BsonDouble(MIN))
                .append("max", new BsonDouble(MAX))
                .append("precision", new BsonInt32(PRECISION));
    }
}
