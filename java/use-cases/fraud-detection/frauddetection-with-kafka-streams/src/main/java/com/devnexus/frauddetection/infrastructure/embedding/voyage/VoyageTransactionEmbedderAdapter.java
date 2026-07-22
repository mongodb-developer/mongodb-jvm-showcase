package com.devnexus.frauddetection.infrastructure.embedding.voyage;

import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.domain.port.TransactionEmbedderPort;
import com.devnexus.frauddetection.infrastructure.embedding.config.VoyageEmbeddingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoyageTransactionEmbedderAdapter implements TransactionEmbedderPort {

	private static final Logger log = LoggerFactory.getLogger(VoyageTransactionEmbedderAdapter.class);

	private final VoyageEmbeddingsClient client;
	private final VoyageEmbeddingProperties config;

	public VoyageTransactionEmbedderAdapter(VoyageEmbeddingsClient client, VoyageEmbeddingProperties config) {
		this.client = client;
		this.config = config;
	}

	@Override
	public Vector embed(Transaction transaction) {
		log.info("[VECTOR SCORING] Validations started");

		String input = TransactionEmbeddingText.toText(transaction);

		log.info("[VECTOR SCORING] Converting transaction to embeddings");

		var res = client.embed(new EmbeddingsRequest(
				List.of(input), config.model(), "query", config.outputDimension()));

		log.info("[VECTOR SCORING] Embeddings generated\tsuccessfully{}", res.data().getFirst().embedding());

		var embedding = res.data().getFirst().embedding();

		float[] result = new float[embedding.size()];
		for (int i = 0; i < embedding.size(); i++) {
			result[i] = embedding.get(i).floatValue();
		}

		return Vector.of(result);
	}
}
