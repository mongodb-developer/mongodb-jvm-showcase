package com.devrel.wms.event;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.service.InventoryService;
import com.devrel.wms.service.ReplenishmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
public class DepositorNotificationListener {

	private final Logger logger = LoggerFactory.getLogger(DepositorNotificationListener.class);
	private final ReplenishmentService replenishmentService;
	private final InventoryService inventoryService;

	DepositorNotificationListener(
			ReplenishmentService replenishmentService,
			InventoryService inventoryService) {
		this.replenishmentService = replenishmentService;
		this.inventoryService = inventoryService;
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void send(ReplenishmentApproved event) {
		String id = event.replenishmentId();
		Replenishment replenishment = replenishmentService.findById(id);

		if (replenishment == null || replenishment.notification() == null) {
			logger.warn("Replenishment {} has no notification draft. Nothing was sent", id);

			return;
		}

		Replenishment.Notification draft = replenishment.notification();
		String recipient = recipient(draft, replenishment.depositor());

		if (recipient == null || recipient.isBlank()) {
			logger.warn("Replenishment {} has no recipient. Nothing was sent", id);

			return;
		}

		logger.info("##EMAIL SENT## - To: {} | Cc: {} | Subject: {}\n{}",
				recipient, copies(draft), draft.subject(), draft.body());

		replenishmentService.saveNotification(id, new Replenishment.Notification(
				recipient, draft.cc(), draft.subject(), draft.body(), LocalDateTime.now()));
	}

	private String copies(Replenishment.Notification draft) {
		return draft.cc() == null || draft.cc().isEmpty() ? "-" : String.join(", ", draft.cc());
	}

	private String recipient(Replenishment.Notification draft, Depositor depositor) {
		if (draft.to() != null && !draft.to().isBlank()) {
			return draft.to();
		}

		if (depositor == null || depositor.id() == null) {
			return null;
		}

		Depositor registered = inventoryService.findDepositorById(depositor.id());

		return registered == null ? null : registered.email();
	}
}
