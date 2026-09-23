package com.example.model.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;

@MongoEntity(collection = "books")
public class Book {

    @BsonId
    public String id;
    public String title;
    public int pages;
    public int year;
    public List<String> authors;
    public List<Review> reviews;
}
