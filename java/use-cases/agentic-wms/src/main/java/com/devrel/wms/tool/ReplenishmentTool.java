package com.devrel.wms.tool;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.Inventory;
import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.domain.StockMovement;
import com.devrel.wms.service.InventoryService;
import com.devrel.wms.service.ReplenishmentService;
import com.devrel.wms.service.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReplenishmentTool {

	private static final Logger logger = LoggerFactory.getLogger(ReplenishmentTool.class);
	private static final String EMAIL_SUBJECT = "Replenishment required for your products";

	private final InventoryService inventoryService;
	private final StockMovementService stockMovementService;
	private final ReplenishmentService replenishmentService;


	public ReplenishmentTool(
			InventoryService inventoryService,
			StockMovementService stockMovementService,
			ReplenishmentService replenishmentService) {
		this.inventoryService = inventoryService;
		this.replenishmentService = replenishmentService;
		this.stockMovementService = stockMovementService;
	}

	@Tool(description = """
   		 Create a replenishment request when one or more products
    	 are expected to run out of stock soon.
    	 Products already covered by a pending request of the same depositor are skipped.
    	 The answer reports the created replenishment id and any skipped product.
    """)
	public String createReplenishment(
			@ToolParam(description = "Depositor that owns the products, taken from the inventory entry")
			Depositor depositor,

			@ToolParam(description = "Products and quantities that need replenishment")
			List<Replenishment.ReplenishmentItem> items,

			@ToolParam(description = "Short explanation of why replenishment is necessary")
			String message
	) {
		logger.info("##TOOL## - Creating replenishment for depositor {}", depositor == null ? null : depositor.id());

		if (depositor == null || depositor.id() == null) {
			return "Depositor is required to create a replenishment.";
		}

		List<Replenishment> pending = replenishmentService.findPendingByDepositor(depositor.id());

		Set<String> covered = pending.stream()
				.flatMap(replenishment -> replenishment.items().stream())
				.map(Replenishment.ReplenishmentItem::productCode)
				.collect(Collectors.toSet());

		List<Replenishment.ReplenishmentItem> newItems = items.stream()
				.filter(item -> !covered.contains(item.productCode()))
				.toList();

		if (newItems.isEmpty()) {
			return "No replenishment created. All requested products are already covered by pending requests: "
					+ pendingReference(pending) + ".";
		}

		Replenishment created = replenishmentService.save(
				new Replenishment(null, depositor, newItems, message, Replenishment.Status.PENDING));

		String skipped = items.stream()
				.map(Replenishment.ReplenishmentItem::productCode)
				.filter(covered::contains)
				.collect(Collectors.joining(", "));

		return "Replenishment %s created for products %s.%s".formatted(
				created.id(),
				newItems.stream()
						.map(Replenishment.ReplenishmentItem::productCode)
						.collect(Collectors.joining(", ")),
				skipped.isBlank() ? "" : " Products already covered by pending requests and skipped: " + skipped + "."
		);
	}

	private String pendingReference(List<Replenishment> pending) {
		return pending.stream()
				.map(Replenishment::id)
				.collect(Collectors.joining(", "));
	}

	@Tool(description = """
    	Get the current inventory quantity for a product, one entry per depositor.
    	Use this tool when you need to know the current available stock.
    """)
	public List<Inventory> getInventoryByProductCode(
			@ToolParam(description = "Unique product code") String productCode) {
		logger.info("##TOOL## - Calling Inventory Product Code for product {}", productCode);
		return inventoryService.findByProductCode(productCode);
	}

	@Tool(description = """
    	Get the stock movement history for a product.
    	Use this tool to analyze recent inbound and outbound quantities and dates.
    """)
	public List<StockMovement> getStockMovement(
			@ToolParam(description = "Unique product code") String productCode) {
		logger.info("##TOOL## - Getting stock movement by product number {}", productCode);

		return stockMovementService.findByProductCode(productCode);
	}

	@Tool(description = """
    	Get the stock movement history for a product of a specific depositor.
    	Use this tool to analyze recent inbound and outbound quantities and dates
    	of a single depositor.
    """)
	public List<StockMovement> getStockMovementByDepositor(
			@ToolParam(description = "Unique product code") String productCode,
			@ToolParam(description = "Unique depositor identifier") String depositorId) {
		logger.info("##TOOL## - Getting stock movement by product {} and depositor {}", productCode, depositorId);

		return stockMovementService.findByProductCodeAndDepositor(productCode, depositorId);
	}

	@Tool(description = """
    	Get the stock movements generated by a specific invoice.
    	Use this tool to discover which products and quantities were affected
    	by a completed outbound invoice.
    """)
	public List<StockMovement> getStockMovementByInvoiceNumber(
			@ToolParam(description = "Outbound invoice number") String invoiceNumber) {
		logger.info("##TOOL## - Getting stock movement by invoice number {}", invoiceNumber);

		return stockMovementService.findByInvoiceNumber(invoiceNumber);
	}

	@Tool(description = """
    	Notify by email the depositor that owns a replenishment request.
    	Use this tool only after a replenishment request has been created,
    	passing the id returned by the creation tool.
    """)
	public String notifyDepositor(
			@ToolParam(description = "Id of the replenishment request to notify about")
			String replenishmentId
	) {
		logger.info("##TOOL## - Notifying depositor about replenishment {}", replenishmentId);

		Replenishment replenishment = replenishmentService.findById(replenishmentId);

		if (replenishment == null) {
			return "Replenishment not found: " + replenishmentId;
		}

		Depositor depositor = resolveDepositor(replenishment.depositor());

		if (depositor == null || depositor.email() == null || depositor.email().isBlank()) {
			return "Depositor has no email registered. Notification was not sent.";
		}

		String email = composeEmail(replenishment, depositor);

		logger.info("##EMAIL## - To: {} <{}> | Subject: {}\n{}",
				depositor.name(), depositor.email(), EMAIL_SUBJECT, email);

		return """
        Email sent to %s <%s>
        Subject: %s

        %s""".formatted(depositor.name(), depositor.email(), EMAIL_SUBJECT, email);
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
		String products = replenishment.items().stream()
				.map(item -> "  - Product " + item.productCode() + ": " + item.quantity() + " unit(s)")
				.collect(Collectors.joining("\n"));

		return """
        Hello %s,

        We are sending you this email because the following products stored in our
        warehouse require replenishment:

        %s

        Reason: %s

        Please arrange a new inbound shipment for these quantities at your earliest
        convenience so we can keep your stock at a healthy level.

        Best regards,
        Agentic WMS Team""".formatted(depositor.name(), products, replenishment.message());
	}
}
