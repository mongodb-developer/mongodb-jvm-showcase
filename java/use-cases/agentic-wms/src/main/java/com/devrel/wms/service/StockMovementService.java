package com.devrel.wms.service;

import com.devrel.wms.domain.DepositorRef;
import com.devrel.wms.domain.StockMovement;
import com.devrel.wms.repository.StockMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {

	private final Logger logger = LoggerFactory.getLogger(StockMovementService.class);
	private final StockMovementRepository stockMovementRepository;

	StockMovementService(StockMovementRepository stockMovementRepository) {
		this.stockMovementRepository = stockMovementRepository;
	}

	public StockMovement register(
			String productCode,
			DepositorRef depositor,
			int quantity,
			String invoiceNumber,
			StockMovement.MovementType type
	) {
		StockMovement save = stockMovementRepository.save(new StockMovement(
				null,
				LocalDateTime.now(),
				productCode,
				depositor,
				quantity,
				invoiceNumber,
				type
		));

		logger.info("Stock movement {} of {} unit(s) for product code {} and depositor {} registered",
				type, quantity, productCode, depositor == null ? null : depositor.id());

		return save;
	}

	public List<StockMovement> findAll() {
		return stockMovementRepository.findAll();
	}

	public List<StockMovement> findByProductCode(String productCode) {
		return stockMovementRepository.findByProductCode(productCode);
	}

	public List<StockMovement> findByProductCodeAndDepositor(String productCode, String depositorId) {
		return stockMovementRepository.findByProductCodeAndDepositorId(productCode, depositorId);
	}

	public List<StockMovement> findByInvoiceNumber(String invoiceNumber) {
		return stockMovementRepository.findByInvoiceNumber(invoiceNumber);
	}
}
