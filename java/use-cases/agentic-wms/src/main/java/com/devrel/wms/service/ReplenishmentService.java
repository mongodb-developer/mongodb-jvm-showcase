package com.devrel.wms.service;

import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.event.ReplenishmentApproved;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.ReplenishmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplenishmentService {

	private final Logger logger = LoggerFactory.getLogger(ReplenishmentService.class);
	private final ReplenishmentRepository replenishmentRepository;
	private final ApplicationEventPublisher eventPublisher;

	ReplenishmentService(
			ReplenishmentRepository replenishmentRepository,
			ApplicationEventPublisher eventPublisher) {
		this.replenishmentRepository = replenishmentRepository;
		this.eventPublisher = eventPublisher;
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

		Replenishment save = replenishmentRepository.save(
				withStatus(replenishment, status, replenishment.notification()));

		logger.info("Replenishment {} moved to {}", save.id(), status);

		if (status == Replenishment.Status.APPROVED) {
			eventPublisher.publishEvent(new ReplenishmentApproved(save.id()));
		}

		return save;
	}

	public Replenishment saveNotification(String id, Replenishment.Notification notification) {
		Replenishment replenishment = replenishmentRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Replenishment not found: " + id));

		Replenishment save = replenishmentRepository.save(
				withStatus(replenishment, replenishment.status(), notification));

		logger.info("Replenishment {} notification drafted", save.id());

		return save;
	}

	private Replenishment withStatus(
			Replenishment replenishment,
			Replenishment.Status status,
			Replenishment.Notification notification
	) {
		return new Replenishment(
				replenishment.id(),
				replenishment.depositor(),
				replenishment.items(),
				replenishment.message(),
				status,
				notification
		);
	}

	private boolean isAllowedTransition(Replenishment.Status current, Replenishment.Status target) {
		return switch (current) {
			case PENDING -> target == Replenishment.Status.APPROVED || target == Replenishment.Status.REJECTED;
			case APPROVED -> target == Replenishment.Status.COMPLETED;
			case REJECTED, COMPLETED -> false;
		};
	}
}
