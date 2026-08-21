package com.devrel.wms.tool;

import com.devrel.wms.domain.DepositorRef;
import com.devrel.wms.domain.Replenishment;
import com.devrel.wms.service.DepositorService;
import com.devrel.wms.service.ReplenishmentService;
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

	private final ReplenishmentService replenishmentService;
	private final ProductCodes productCodes;
	private final DepositorService depositorService;

	ReplenishmentTool(
			ReplenishmentService replenishmentService,
			ProductCodes productCodes,
			DepositorService depositorService) {
		this.replenishmentService = replenishmentService;
		this.productCodes = productCodes;
		this.depositorService = depositorService;
	}

	@Tool(description = """
   		 Create a replenishment request when one or more products
    	 are expected to run out of stock soon.
    	 Products already covered by a pending request of the same depositor are skipped.
    	 The answer reports the created replenishment id and any skipped product.
    """)
	public String createReplenishment(
			@ToolParam(description = ProductCodes.DEPOSITOR_CODE_PARAM)
			String depositorCode,

			@ToolParam(description = "Products and quantities that need replenishment. "
					+ "Each product code must be the exact code as stored, for example '02'")
			List<Replenishment.ReplenishmentItem> items,

			@ToolParam(description = "Short explanation of why replenishment is necessary")
			String message
	) {
		logger.info("##TOOL## - Creating replenishment for depositor {}", depositorCode);

		DepositorRef resolved = depositorService.toRef(new DepositorRef(null, depositorCode, null));

		items.forEach(item -> productCodes.require(item.productCode()));

		List<Replenishment> pending = replenishmentService.findPendingByDepositor(resolved.id());

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
				new Replenishment(null, resolved, newItems, message, Replenishment.Status.PENDING, null));

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
}
