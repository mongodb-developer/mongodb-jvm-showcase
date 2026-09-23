package com.example.model.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;

@MongoEntity(collection = "reviews")
public class Review {

    @BsonId
    public String id;
    public String text;
    public String user;
    public double rating;
    public String bookId;
    public Instant createdAt;
}
