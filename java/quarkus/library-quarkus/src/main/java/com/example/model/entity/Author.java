package com.example.model.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;

/**
 * Persistence model for an author.
 *
 * <p><strong>Active Record pattern (deliberate teaching contrast).</strong>
 * Unlike {@code Book} and {@code Review} — which use the Repository pattern via
 * {@code PanacheMongoRepositoryBase} — {@code Author} extends
 * {@link PanacheMongoEntityBase}, so persistence and queries live on the entity
 * itself (e.g. {@code Author.listAll()}, {@code author.persist()}). This sample
 * intentionally shows both Panache styles side by side; pick one per project for
 * production code. {@link PanacheMongoEntityBase} (rather than
 * {@code PanacheMongoEntity}) is used so the document keeps a {@code String}
 * {@code _id} instead of the default {@code ObjectId}.
 */
@MongoEntity(collection = "authors")
public class Author extends PanacheMongoEntityBase {

    @BsonId
    public String id;
    public String name;
    public String nationality;
    public int birthYear;

    public static List<Author> findByNationality(String nationality) {
        return list("nationality", nationality);
    }
}
