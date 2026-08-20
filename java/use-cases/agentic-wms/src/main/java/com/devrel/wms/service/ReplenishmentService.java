package com.devrel.wms.service;

import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.ReplenishmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplenishmentService {

	private final Logger logger = LoggerFactory.getLogger(ReplenishmentService.class);
	ReplenishmentRepository replenishmentRepository;

	ReplenishmentService(ReplenishmentRepository replenishmentRepository) {
		this.replenishmentRepository = replenishmentRepository;
	}

	public Replenishment save(Replenishment replenishment) {
		Replenishment save = replenishmentRepository.save(replenishment);

		logger.info("Replenishment with Id {} saved", save.id());

		return save;
	}

	public List<Replenishment> findAll() {
		return replenishmentRepository.findAll();
	}

	public Replenishment findById(String id) {
		return replenishmentRepository.findById(id).orElse(null);
	}

	public List<Replenishment> findPendingByDepositor(String depositorId) {
		return replenishmentRepository.findByDepositorIdAndStatus(depositorId, Replenishment.Status.PENDING);
	}

	public Replenishment changeStatus(String id, Replenishment.Status status) {
		Replenishment replenishment = replenishmentRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Replenishment not found: " + id));

		if (!isAllowedTransition(replenishment.status(), status)) {
			throw new ConflictException(
					"Cannot change replenishment status from " + replenishment.status() + " to " + status);
		}

		Replenishment save = replenishmentRepository.save(new Replenishment(
				replenishment.id(),
				replenishment.depositor(),
				replenishment.items(),
				replenishment.message(),
				status
		));

		logger.info("Replenishment {} moved to {}", save.id(), status);

		return save;
	}

	private boolean isAllowedTransition(Replenishment.Status current, Replenishment.Status target) {
		return switch (current) {
			case PENDING -> target == Replenishment.Status.APPROVED || target == Replenishment.Status.REJECTED;
			case APPROVED -> target == Replenishment.Status.COMPLETED;
			case REJECTED, COMPLETED -> false;
		};
	}
}
