package com.mongodb.devrel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, java.util.UUID> {
}
