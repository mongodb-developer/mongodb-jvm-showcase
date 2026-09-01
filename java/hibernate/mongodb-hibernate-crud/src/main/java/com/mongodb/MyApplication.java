package com.mongodb;

import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Book;
import com.mongodb.domain.Order;
import com.mongodb.domain.OrderItem;
import com.mongodb.domain.Review;
import com.mongodb.service.BookService;
import com.mongodb.service.OrderService;
import com.mongodb.service.ReviewService;
import org.bson.types.ObjectId;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Scanner;

public class MyApplication {

	private static final Scanner SCANNER = new Scanner(System.in);
	private static int option;

	public static void main(String[] args) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		do {
			System.out.println("\n=== Welcome to Hibernate App ===");
			System.out.println("1 - Manage Books");
			System.out.println("2 - Manage Orders");
			System.out.println("0 - Exit");

			System.out.print("Choose: ");
			option = SCANNER.nextInt();
			SCANNER.nextLine();

			switch (option) {
				case 1 -> manageBooks();
				case 2 -> manageOrders();

				case 0 -> System.out.println("Bye!");
				default -> System.out.println("Invalid option, try again.");
			}


		} while (option != 0);

		SCANNER.close();
		factory.close();
	}

	private static void manageOrders() {
		OrderService orderService = new OrderService();

		System.out.println("1 - Create Order");
		System.out.println("2 - List All orders");
		System.out.println("3 - List Order by number");
		System.out.println("4 - Update Order");
		System.out.println("5 - Delete Order");

		System.out.println("0 - Exit");

		System.out.print("Choose: ");
		option = SCANNER.nextInt();
		SCANNER.nextLine();

		switch (option) {
			case 1 -> {
				System.out.print("Customer: ");
				String customer = SCANNER.nextLine();

				System.out.print("Order Number: ");
				String number = SCANNER.nextLine();

				System.out.print("Customer Address - City:");
				String city = SCANNER.nextLine();

				System.out.print("Customer Address - Zipcode:");
				String zipcode = SCANNER.nextLine();

				Order order = new Order();
				order.setCustomer(customer);
				order.setNumber(number);
				order.setPaid(false);
				order.setAddress(new Order.Address(city, zipcode));

				while (true) {
					System.out.print("Item name: ");
					String product = SCANNER.nextLine();

					System.out.print("Quantity: ");
					String quantity = SCANNER.nextLine();

					System.out.print("Price: ");
					String price = SCANNER.nextLine();

					OrderItem orderItem =
							new OrderItem(
									product,
									Integer.parseInt(quantity),
									Double.parseDouble(price)
							);

					order.addItem(orderItem);

					System.out.print("There's more items? Yes or No ");
					String more = SCANNER.nextLine();

					if (more.equalsIgnoreCase("No")) {
						break;
					}
				}

				orderService.save(order);
				System.out.println("Order created!");
			}
			case 2 -> {
				List<Order> orders = orderService.listAll();
				for (Order o : orders) {
					System.out.println(o);
				}
			}
			case 3 -> {
				System.out.print("Order number: ");
				String orderNumber = SCANNER.nextLine();
				List<OrderService.OrderReturned> order = orderService.findByOrderNumber(orderNumber);

				for (OrderService.OrderReturned o : order) {
					System.out.println(o);
				}
			}

			case 4 -> {
				System.out.print("Order number: ");
				String orderNumber = SCANNER.nextLine();

				System.out.println(orderService.markAsPaid(orderNumber)
						? "Order paid!"
						: "Order not found!");
			}

			case 5 -> {
				System.out.print("Order number: ");
				String orderNumber = SCANNER.nextLine();

				if (orderService.deleteById(orderNumber) > 0) {
					System.out.println("Order deleted!");
				} else
					System.out.println("Order not found!");
			}

			case 0 -> System.out.println("Bye!");
		}

	}

	private static void manageBooks() {
		BookService bookService = new BookService();
		ReviewService reviewService = new ReviewService();

		System.out.println("1 - Create Book");
		System.out.println("2 - List Book");
		System.out.println("3 - Update Book");
		System.out.println("4 - Delete Book");
		System.out.println("5 - Find Books by Minimum Pages");
		System.out.println("6 - Add Review");
		System.out.println("7 - List Books and Reviews by Id");

		System.out.println("0 - Exit");

		System.out.print("Choose: ");
		option = SCANNER.nextInt();
		SCANNER.nextLine();

		switch (option) {
			case 1 -> {
				System.out.print("Title: ");
				String title = SCANNER.nextLine();

				System.out.print("Number of pages: ");
				int pages = SCANNER.nextInt();
				SCANNER.nextLine();

				var book = bookService.create(title, pages);
				System.out.println("Created: " + book);
			}

			case 2 -> {
				List<Book> books = bookService.findAll();
				books.forEach(System.out::println);
			}

			case 3 -> {
				System.out.print("Book ID: ");
				String id = SCANNER.nextLine();

				System.out.print("New Title: ");
				String newTitle = SCANNER.nextLine();

				System.out.print("New Page Count: ");
				int newPages = SCANNER.nextInt();
				SCANNER.nextLine();

				Book updated = new Book(new ObjectId(id), newTitle, newPages);
				boolean ok = bookService.update(updated);
				System.out.println(ok ? "Book updated successfully!" : "Book not found.");
			}

			case 4 -> {
				System.out.print("Book ID to delete: ");
				String id = SCANNER.nextLine();
				boolean deleted = bookService.deleteById(new ObjectId(id));
				System.out.println(deleted ? "Book deleted successfully!" : "Book not found.");
			}
			case 5 -> {
				System.out.print("Enter the minimum number of pages: ");
				String pages = SCANNER.nextLine();
				List<Book> books = bookService.findBooksWithPagesGreaterThanOrEqual(Integer.parseInt(pages));

				books.forEach(System.out::println);
			}
			case 6 -> {
				System.out.print("Book ID: ");
				String bookId = SCANNER.nextLine();
				System.out.print("Author: ");
				String author = SCANNER.nextLine();
				System.out.print("Review Title: ");
				String rTitle = SCANNER.nextLine();
				System.out.print("Comment: ");
				String comment = SCANNER.nextLine();
				System.out.print("Rating: ");
				double rating = SCANNER.nextDouble();
				SCANNER.nextLine();

				reviewService.insert(new Review(author, new ObjectId(bookId), rTitle, comment, rating));
				System.out.println("Review added!");
			}

			case 7 -> {
				System.out.print("Book ID: ");
				String bookId = SCANNER.nextLine();
				BookService.BookWithReviews br = bookService.findAllBooksWithReviewsById(new ObjectId(bookId));

				if (br == null) {
					System.out.println("Book not found.");
				} else {
					System.out.printf("\n%s - %s (%d reviews)\n",
							br.book().getId(), br.book().getTitle(), br.reviews().size());
					br.reviews().forEach(System.out::println);
				}
			}
		}

	}
}
