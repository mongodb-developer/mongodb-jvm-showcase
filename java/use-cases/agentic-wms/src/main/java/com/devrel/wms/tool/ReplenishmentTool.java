package com.devrel.wms.tool;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.Inventory;
import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.domain.StockMovement;
import com.devrel.wms.service.InventoryService;
import com.devrel.wms.service.ProductService;
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

	private static final String PRODUCT_CODE_PARAM = """
			Exact product code as stored in the system, for example '02'.
			Never include words or labels such as 'Product', 'SKU' or the product name.""";

	private static final String DEPOSITOR_ID_PARAM = """
			Exact depositor identifier as stored in the system, for example 'bsp'.
			Never use the depositor name.""";

	private final InventoryService inventoryService;
	private final StockMovementService stockMovementService;
	private final ReplenishmentService replenishmentService;
	private final ProductService productService;


	public ReplenishmentTool(
			InventoryService inventoryService,
			StockMovementService stockMovementService,
			ReplenishmentService replenishmentService,
			ProductService productService) {
		this.inventoryService = inventoryService;
		this.replenishmentService = replenishmentService;
		this.stockMovementService = stockMovementService;
		this.productService = productService;
	}

	private String requireProductCode(String productCode) {
		if (productCode != null && productService.findByCode(productCode) != null) {
			return productCode;
		}

		throw new IllegalArgumentException(
				"Unknown product code: '" + productCode + "'. Use the exact code as stored, "
						+ "for example '02'. Do not add any word or label to the code.");
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

			@ToolParam(description = "Products and quantities that need replenishment. "
					+ "Each product code must be the exact code as stored, for example '02'")
			List<Replenishment.ReplenishmentItem> items,

			@ToolParam(description = "Short explanation of why replenishment is necessary")
			String message
	) {
		logger.info("##TOOL## - Creating replenishment for depositor {}", depositor == null ? null : depositor.id());

		if (depositor == null || depositor.id() == null) {
			return "Depositor is required to create a replenishment.";
		}

		items.forEach(item -> requireProductCode(item.productCode()));

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
				new Replenishment(null, depositor, newItems, message, Replenishment.Status.PENDING, null));

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
			@ToolParam(description = PRODUCT_CODE_PARAM) String productCode) {
		logger.info("##TOOL## - Calling Inventory Product Code for product {}", productCode);

		return inventoryService.findByProductCode(requireProductCode(productCode));
	}

	@Tool(description = """
    	Get the stock movement history for a product.
    	Use this tool to analyze recent inbound and outbound quantities and dates.
    """)
	public List<StockMovement> getStockMovement(
			@ToolParam(description = PRODUCT_CODE_PARAM) String productCode) {
		logger.info("##TOOL## - Getting stock movement by product number {}", productCode);

		return stockMovementService.findByProductCode(requireProductCode(productCode));
	}

	@Tool(description = """
    	Get the stock movement history for a product of a specific depositor.
    	Use this tool to analyze recent inbound and outbound quantities and dates
    	of a single depositor.
    """)
	public List<StockMovement> getStockMovementByDepositor(
			@ToolParam(description = PRODUCT_CODE_PARAM) String productCode,
			@ToolParam(description = DEPOSITOR_ID_PARAM) String depositorId) {
		logger.info("##TOOL## - Getting stock movement by product {} and depositor {}", productCode, depositorId);

		return stockMovementService.findByProductCodeAndDepositor(requireProductCode(productCode), depositorId);
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
    	Write the notification email that will be sent to the depositor of a replenishment request.
    	The email is only drafted and stored, never sent by this tool.
    	It is sent later, when a warehouse operator approves the replenishment request.
    	Use this tool only after a replenishment request has been created,
    	passing the id returned by the creation tool.
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
				recipient, EMAIL_SUBJECT, body, null));

		return """
        Email drafted for replenishment %s. It will be sent when the request is approved.
        To: %s
        Subject: %s

        %s""".formatted(replenishmentId, recipient == null ? "to be resolved on approval" : recipient, EMAIL_SUBJECT, body);
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

		String name = depositor == null || depositor.name() == null ? "depositor" : depositor.name();

		return """
        Hello %s,

        We are contacting you because the following products stored in our
        warehouse require replenishment:

        %s

        Reason: %s

        Please arrange a new inbound shipment for these quantities at your earliest
        convenience so we can keep your stock at a healthy level.

        Best regards,
        Agentic WMS Team""".formatted(name, products, replenishment.message());
	}
}
