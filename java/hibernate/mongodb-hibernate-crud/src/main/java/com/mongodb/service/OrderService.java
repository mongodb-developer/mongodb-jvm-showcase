package com.mongodb.service;


import com.mongodb.config.HibernateUtil;
import com.mongodb.domain.Order;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OrderService {

	public void save(Order order) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			session.persist(order);
			tx.commit();
		}
	}

	public boolean markAsPaid(String orderNumber) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();

			Order order = session.createSelectionQuery(
							"from Order o where o.number = :orderNumber",
							Order.class
					)
					.setParameter("orderNumber", orderNumber)
					.uniqueResult();

			if (order == null) {
				tx.rollback();
				return false;
			}

			order.setPaid(true);
			tx.commit();

			return true;
		}
	}

	public List<OrderReturned> findByOrderNumber(String orderNumber) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			return session.createQuery("""
				select o.customer, o.number, item.product, item.quantity
				from Order o
				join o.items item
				where o.number = :orderNumber
				order by item.price asc
				""", OrderReturned.class)
			.setParameter("orderNumber", orderNumber)
					.getResultList();
		}
	}

	public int deleteById(String orderNumber) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			try {
				Order order = session.createSelectionQuery("""
                    from Order o
                    where o.number = :orderNumber
                    """, Order.class)
						.setParameter("orderNumber", orderNumber)
						.uniqueResult();

				if (order == null) {
					transaction.rollback();
					return 0;
				}

				session.remove(order);
				transaction.commit();

				return 1;
			} catch (RuntimeException exception) {
				if (transaction.isActive()) {
					transaction.rollback();
				}

				throw exception;
			}
		}
	}

	public List<Order> listAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("""
					select distinct o
                   from Order o
                   left join fetch o.items
			""", Order.class).getResultList();
		}
	}

	public record OrderReturned(String customer, String number, String product, Integer quantity) {}

}
