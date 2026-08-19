package com.devrel.wms.repository;

import com.devrel.wms.entity.StockMovement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
	List<StockMovement> findByProductCode(String productCode);

	List<StockMovement> findByInvoiceNumber(String invoiceNumber);
}