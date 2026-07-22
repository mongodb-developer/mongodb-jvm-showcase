package com.devnexus.frauddetection.domain.port;

import com.devnexus.frauddetection.domain.model.Transaction;
import org.springframework.data.domain.Vector;

public interface TransactionEmbedderPort {
    Vector embed(Transaction transaction);
}
