package com.mongodb.domain;

import com.mongodb.hibernate.annotations.ObjectIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.bson.types.ObjectId;

@Entity
@Table(name = "reviews")
public class Review {
   @Id
   @ObjectIdGenerator
   ObjectId id;
   private ObjectId bookId;
   private String author;
   private String title;
   private String comment;
   private double rating;

   public Review() {}
   public Review(String author, ObjectId bookId, String title, String comment, double rating) {
      this.author = author;
      this.bookId = bookId;
      this.title = title;
      this.comment = comment;
      this.rating = rating;
   }

   @Override
   public String toString() {
      return "Review{author='%s', title='%s', comment='%s', rating=%.1f}"
              .formatted(author, title, comment, rating);
   }

   public String getComment() {
      return comment;
   }

   public ObjectId getBookId() {
      return bookId;
   }

   public String getAuthor() {
      return author;
   }
}
