package com.mongodb.domain;

import com.mongodb.hibernate.annotations.ObjectIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
	@Id
	@ObjectIdGenerator
	ObjectId id;
	String title;
	Integer pages;

	List<RecentReview> recentReview = new ArrayList<>();

	public Book() {}
	public Book(String title, Integer pages) {
		this.title = title;
		this.pages = pages;
	}

	public Book(ObjectId id, String title, Integer pages) {
		this.id = id;
		this.title = title;
		this.pages = pages;
	}

	public ObjectId getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Integer getPages() {
		return pages;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return "Book{id=%s, title='%s', totalPages='%s', 'recentReviews='%s'}"
				.formatted(id, title, pages, recentReview);
	}
}
