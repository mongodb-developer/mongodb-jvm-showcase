package com.mongodb.service;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Book;
import com.mongodb.domain.Review;
import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BookService {

	public Book create(String title, Integer pages) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			Book book = new Book(title, pages);
			session.persist(book);
			tx.commit();
			return book;
		}
	}

	public List<Book> findAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("from Book", Book.class).list();
		}
	}

	public boolean update(Book updatedBook) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();

			Book existing = session.find(Book.class, updatedBook.getId());
			if (existing == null) return false;

			existing.setTitle(updatedBook.getTitle());
			existing.setPages(updatedBook.getPages());

			session.merge(existing);
			tx.commit();
			return true;
		}
	}

	public boolean deleteById(ObjectId id) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();

			Book book = session.find(Book.class, id);
			if (book == null) {
				return false;
			}

			session.remove(book);
			tx.commit();
			return true;
		}
	}

	public List<Book> findBooksWithPagesGreaterThanOrEqual(int minPages) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery(
							"from Book b where b.pages >= :minPages", Book.class)
					.setParameter("minPages", minPages)
					.list();
		}
	}
	public BookWithReviews findAllBooksWithReviewsById(ObjectId id) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			Book book = session.find(Book.class, id);
			if (book == null) {
				return null;
			}

			List<Review> reviews = session.createQuery(
							"from Review r where r.bookId = :bookId", Review.class)
					.setParameter("bookId", book.getId())
					.list();

			return new BookWithReviews(book, reviews);
		}
	}

	public record BookWithReviews(Book book, List<Review> reviews) {}
}