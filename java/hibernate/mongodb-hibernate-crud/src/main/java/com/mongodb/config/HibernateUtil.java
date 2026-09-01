package com.mongodb.config;

import com.mongodb.domain.Book;
import com.mongodb.domain.Order;
import com.mongodb.domain.OrderItem;
import com.mongodb.domain.Review;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
   private static final SessionFactory SESSION_FACTORY =
         new Configuration().configure("hibernate.cfg.xml")
               .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Review.class)
                 .addAnnotatedClass(Order.class)
                 .addAnnotatedClass(OrderItem.class)
               .buildSessionFactory();

   private HibernateUtil() {}

   public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }

}
