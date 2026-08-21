package com.devrel.wms.tool;

import com.devrel.wms.agent.AgentLanguage;
import com.devrel.wms.agent.AgentLanguageSettings;
import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.service.InventoryService;
import com.devrel.wms.service.ReplenishmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DepositorEmailTool {

	private static final Logger logger = LoggerFactory.getLogger(DepositorEmailTool.class);
	private static final String EMAIL_SUBJECT = "Replenishment required for your products";
	private static final String EMAIL_SUBJECT_PT_BR = "Reabastecimento necessário para seus produtos";

	private final ReplenishmentService replenishmentService;
	private final InventoryService inventoryService;
	private final AgentLanguageSettings agentLanguageSettings;

	DepositorEmailTool(
			ReplenishmentService replenishmentService,
			InventoryService inventoryService,
			AgentLanguageSettings agentLanguageSettings) {
		this.replenishmentService = replenishmentService;
		this.inventoryService = inventoryService;
		this.agentLanguageSettings = agentLanguageSettings;
	}

	private String subject() {
		return agentLanguageSettings.language() == AgentLanguage.PT_BR ? EMAIL_SUBJECT_PT_BR : EMAIL_SUBJECT;
	}

	@Tool(description = """
    	Write the notification email that will be sent to the depositor of a replenishment request.
    	The email is only drafted and stored, never sent by this tool.
    	It is sent later, when a warehouse operator approves the replenishment request.
    	Use this tool only after a replenishment request has been created,
    	passing the id returned by the creation tool.
    	Each replenishment request has a single email: calling this tool again for the same
    	request changes nothing.
    """)
	public String draftDepositorEmail(
			@ToolParam(description = "Id of the replenishment request to write the email for")
			String replenishmentId
	) {
		logger.info("##TOOL## - Drafting depositor email for replenishment {}", replenishmentId);

		Replenishment replenishment = replenishmentService.findById(replenishmentId);

		if (replenishment == null) {
			return "Replenishment not found: " + replenishmentId;
		}

		if (replenishment.notification() != null) {
			return "Email already drafted for replenishment %s. Nothing was changed. It will be sent when the request is approved."
					.formatted(replenishmentId);
		}

		Depositor depositor = resolveDepositor(replenishment.depositor());
		String recipient = depositor == null ? null : depositor.email();
		String body = composeEmail(replenishment, depositor);

		replenishmentService.saveNotification(replenishmentId, new Replenishment.Notification(
				recipient, subject(), body, null));

		return """
        Email drafted for replenishment %s. It will be sent when the request is approved.
        To: %s
        Subject: %s

        %s""".formatted(replenishmentId, recipient == null ? "to be resolved on approval" : recipient, subject(), body);
	}

	private Depositor resolveDepositor(Depositor depositor) {
		if (depositor == null || depositor.id() == null) {
			return depositor;
		}

		if (depositor.email() != null && !depositor.email().isBlank()) {
			return depositor;
		}

		Depositor registered = inventoryService.findDepositorById(depositor.id());

		if (registered == null) {
			return depositor;
		}

		logger.info("Depositor {} email resolved from inventory", depositor.id());

		return registered;
	}

	private String composeEmail(Replenishment replenishment, Depositor depositor) {
		if (agentLanguageSettings.language() == AgentLanguage.PT_BR) {
			return composePortugueseEmail(replenishment, depositor);
		}

		String products = replenishment.items().stream()
				.map(item -> "  - Product " + item.productCode() + ": " + item.quantity() + " unit(s)")
				.collect(Collectors.joining("\n"));

		return """
        Hello %s,

        We are contacting you because the following products stored in our
        warehouse require replenishment:

        %s

        Reason: %s

        Please arrange a new inbound shipment for these quantities at your earliest
        convenience so we can keep your stock at a healthy level.

        Best regards,
        Agentic WMS Team""".formatted(depositorName(depositor), products, replenishment.message());
	}

	private String composePortugueseEmail(Replenishment replenishment, Depositor depositor) {
		String products = replenishment.items().stream()
				.map(item -> "  - Produto " + item.productCode() + ": " + item.quantity() + " unidade(s)")
				.collect(Collectors.joining("\n"));

		return """
        Olá %s,

        Estamos entrando em contato porque os seguintes produtos armazenados em
        nosso centro de distribuição precisam de reabastecimento:

        %s

        Motivo: %s

        Por favor, providencie uma nova remessa de entrada com essas quantidades o
        quanto antes, para mantermos seu estoque em um nível saudável.

        Atenciosamente,
        Equipe Agentic WMS""".formatted(depositorName(depositor), products, replenishment.message());
	}

	private String depositorName(Depositor depositor) {
		return depositor == null || depositor.name() == null ? "depositor" : depositor.name();
	}
}
