package com.devrel.wms.tool;

import com.devrel.wms.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class ProductCodes {

	static final String PRODUCT_CODE_PARAM = """
			Exact product code as stored in the system, for example '02'.
			Never include words or labels such as 'Product', 'SKU' or the product name.""";

	static final String DEPOSITOR_ID_PARAM = """
			Exact depositor identifier as stored in the system, for example 'bsp'.
			Never use the depositor name.""";

	private final ProductService productService;

	ProductCodes(ProductService productService) {
		this.productService = productService;
	}

	String require(String productCode) {
		if (productCode != null && productService.findByCode(productCode) != null) {
			return productCode;
		}

		throw new IllegalArgumentException(
				"Unknown product code: '" + productCode + "'. Use the exact code as stored, "
						+ "for example '02'. Do not add any word or label to the code.");
	}
}
