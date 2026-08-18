package com.devrel.wms.repository;

import com.devrel.wms.entity.OutboundInvoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutboundInvoiceRepository extends MongoRepository<OutboundInvoice, String> {
	Optional<OutboundInvoice> findByNumber(String number);
}