package com.devrel.wms.repository;

import com.devrel.wms.entity.InboundInvoice;
import com.devrel.wms.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InboundInvoiceRepository extends MongoRepository<InboundInvoice, String> {
	Optional<InboundInvoice> findByNumber(String number);
}
