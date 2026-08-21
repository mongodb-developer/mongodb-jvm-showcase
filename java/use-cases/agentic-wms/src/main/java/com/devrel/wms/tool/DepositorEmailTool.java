package com.devrel.wms.tool;

import com.devrel.wms.agent.AgentLanguage;
import com.devrel.wms.agent.AgentLanguageSettings;
import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.DepositorRef;
import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.knowledge.DepositorKnowledgeRepository;
import com.devrel.wms.service.DepositorService;
import com.devrel.wms.service.ReplenishmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DepositorEmailTool {

	private static final Logger logger = LoggerFactory.getLogger(DepositorEmailTool.class);
	private static final String EMAIL_SUBJECT = "Replenishment required for your products";
	private static final String EMAIL_SUBJECT_PT_BR = "Reabastecimento necessário para seus produtos";
	private static final Set<String> COPY_ATTRIBUTES = Set.of("email", "emails", "cc", "copy");

	private final ReplenishmentService replenishmentService;
	private final DepositorService depositorService;
	private final AgentLanguageSettings agentLanguageSettings;
	private final DepositorKnowledgeRepository depositorKnowledgeRepository;

	DepositorEmailTool(
			ReplenishmentService replenishmentService,
			DepositorService depositorService,
			AgentLanguageSettings agentLanguageSettings,
			DepositorKnowledgeRepository depositorKnowledgeRepository) {
		this.replenishmentService = replenishmentService;
		this.depositorService = depositorService;
		this.agentLanguageSettings = agentLanguageSettings;
		this.depositorKnowledgeRepository = depositorKnowledgeRepository;
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
			@ToolParam(description = ReplenishmentIds.ID_PARAM)
			String id
	) {
		String replenishmentId = ReplenishmentIds.sanitize(id);

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
		List<String> copies = copyList(depositor, recipient);
		String body = composeEmail(replenishment, depositor);

		replenishmentService.saveNotification(replenishmentId, new Replenishment.Notification(
				recipient, copies, subject(), body, null));

		return """
        Email drafted for replenishment %s. It will be sent when the request is approved.
        To: %s
        %sSubject: %s

        %s""".formatted(
				replenishmentId,
				recipient == null ? "to be resolved on approval" : recipient,
				copies.isEmpty() ? "" : "Cc: " + String.join(", ", copies) + "\n",
				subject(),
				body);
	}

	private List<String> copyList(Depositor depositor, String recipient) {
		if (depositor == null || depositor.id() == null) {
			return List.of();
		}

		Set<String> copies = new LinkedHashSet<>();

		depositorKnowledgeRepository.findByDepositorId(depositor.id()).forEach(entry -> {
			if (entry.attributes() == null) {
				return;
			}

			entry.attributes().forEach((name, value) -> {
				if (COPY_ATTRIBUTES.contains(name)) {
					copies.addAll(addresses(value));
				}
			});
		});

		copies.remove(recipient);

		if (!copies.isEmpty()) {
			logger.info("Depositor {} policies require copying {}", depositor.id(), copies);
		}

		return List.copyOf(copies);
	}

	private List<String> addresses(Object value) {
		if (value == null) {
			return List.of();
		}

		if (value instanceof Iterable<?> values) {
			List<String> result = new ArrayList<>();
			values.forEach(item -> result.addAll(addresses(item)));

			return result;
		}

		return Arrays.stream(String.valueOf(value).split("[,;]"))
				.map(String::trim)
				.filter(address -> address.contains("@"))
				.toList();
	}

	private Depositor resolveDepositor(DepositorRef depositor) {
		if (depositor == null || depositor.id() == null) {
			return null;
		}

		Depositor registered = depositorService.findById(depositor.id());

		if (registered == null) {
			logger.warn("Depositor {} is not registered. The email has no recipient", depositor.id());

			return new Depositor(depositor.id(), null, depositor.name(), null);
		}

		logger.info("Depositor {} resolved from the depositors collection", depositor.id());

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
