package com.devnexus.frauddetection.infrastructure.http;

import com.devnexus.frauddetection.application.request.TransactionRequest;
import com.devnexus.frauddetection.application.usecase.ProduceTransactionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

 	private final ProduceTransactionUseCase produceTransactionUseCase;

	TransactionController(ProduceTransactionUseCase produceTransactionUseCase) {
		this.produceTransactionUseCase = produceTransactionUseCase;
	}

	@PostMapping
	public ResponseEntity<String> create(@RequestBody TransactionRequest transactionRequest) {
		produceTransactionUseCase.execute(transactionRequest);
		return ResponseEntity.ok("Transaction sent to Kafka!");
	}
}