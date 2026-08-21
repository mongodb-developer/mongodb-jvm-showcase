package com.devrel.wms.service;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.knowledge.DepositorKnowledgeEntry;
import com.devrel.wms.knowledge.DepositorKnowledgeRepository;
import com.devrel.wms.knowledge.DepositorKnowledgeStore;
import com.devrel.wms.knowledge.KnowledgeType;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepositorPolicyService {

	private static final int PREVIEW_TOP_K = 1;
	private static final List<KnowledgeType> PREVIEW_TYPES =
			List.of(KnowledgeType.REPLENISHMENT, KnowledgeType.INBOUND, KnowledgeType.GENERAL);

	private final DepositorKnowledgeRepository depositorKnowledgeRepository;
	private final DepositorKnowledgeStore depositorKnowledgeStore;

	DepositorPolicyService(
			DepositorKnowledgeRepository depositorKnowledgeRepository,
			DepositorKnowledgeStore depositorKnowledgeStore) {
		this.depositorKnowledgeRepository = depositorKnowledgeRepository;
		this.depositorKnowledgeStore = depositorKnowledgeStore;
	}

	public List<DepositorKnowledgeEntry> findAll() {
		return depositorKnowledgeRepository.findAll();
	}

	public List<DepositorKnowledgeEntry> findByDepositorId(String depositorId) {
		return depositorKnowledgeRepository.findByDepositorId(depositorId);
	}

	public List<Depositor> findDepositors() {
		Map<String, Depositor> depositors = new LinkedHashMap<>();

		depositorKnowledgeRepository.findDepositors().stream()
				.filter(depositor -> depositor.id() != null)
				.forEach(depositor -> depositors.put(depositor.id(), depositor));

		depositorKnowledgeRepository.findAll().stream()
				.map(DepositorKnowledgeEntry::depositorId)
				.filter(id -> id != null && !depositors.containsKey(id))
				.forEach(id -> depositors.put(id, new Depositor(id, id, null)));

		List<Depositor> result = new ArrayList<>(depositors.values());
		result.sort(Comparator.comparing(Depositor::id));

		return result;
	}

	public DepositorKnowledgeEntry save(DepositorKnowledgeEntry entry) {
		if (isBlank(entry.depositorId()) || isBlank(entry.key()) || isBlank(entry.text())) {
			throw new IllegalArgumentException("Depositor, key and text are required to save a policy.");
		}

		DepositorKnowledgeEntry normalized = new DepositorKnowledgeEntry(
				entry.depositorId().trim(),
				entry.key().trim(),
				entry.type() == null ? KnowledgeType.GENERAL : entry.type(),
				entry.text().trim(),
				entry.attributes() == null ? Map.of() : entry.attributes()
		);

		Document document = normalized.toDocument();

		depositorKnowledgeStore.delete(List.of(document.getId()));
		depositorKnowledgeStore.save(List.of(document));

		return normalized;
	}

	public void delete(String depositorId, String key) {
		depositorKnowledgeStore.delete(List.of(depositorId + ":" + key));
	}

	public List<String> preview(String depositorId, String question) {
		return depositorKnowledgeStore
				.search(question, depositorId, PREVIEW_TYPES, PREVIEW_TOP_K)
				.stream()
				.map(Document::getText)
				.toList();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
