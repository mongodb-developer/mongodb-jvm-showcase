package com.mongodb.service;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Book;
import com.mongodb.domain.Review;
import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ReviewService {

   public void insert(Review review) {
      try (Session session = HibernateUtil.getSessionFactory().openSession()) {
         Transaction tx = session.beginTransaction();
         session.persist(review);
         addRecentReview(review.getBookId(), review, session);
         System.out.println("Review inserted: " + review);
         tx.commit();
      }
   }

   private void addRecentReview(ObjectId bookId, Review review, Session session) {
      var mql = """
           {
               "update": "books",
               "updates": [{
                   "q": { "_id": { "$oid": "%s" } },
                   "u": {
                       "$push": {
                           "recentReview": {
                               "$each": [{
                                   "author": "%s",
                                   "comment": "%s"
                               }],
                               "$slice": -3
                           }
                       }
                   }
               }]
           }
           """.formatted(
              bookId.toString(),
              escapeJson(review.getAuthor()),
              escapeJson(review.getComment())
      );

      System.out.println(session.createNativeQuery(mql, Book.class).executeUpdate() + " document updated.");
   }

   private String escapeJson(String input) {
      if (input == null) return "";
      return input.replace("\\", "\\\\")
              .replace("\"", "\\\"")
              .replace("\n", "\\n")
              .replace("\r", "\\r")
              .replace("\t", "\\t");
   }
}