package com.mongodb.devrel;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {

	List<Order> findByCustomerName(String customerName);

	@Query("select o from Order o where o.customerName = :customerName")
	List<Order> searchByCustomerName(@Param("customerName") String customerName);
}
