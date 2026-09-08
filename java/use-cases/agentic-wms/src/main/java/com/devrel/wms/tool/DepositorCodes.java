package com.devrel.wms.tool;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.service.DepositorService;
import org.springframework.stereotype.Component;

@Component
public class DepositorCodes {

	static final String DEPOSITOR_CODE_PARAM = """
			Exact depositor code as stored in the system, for example 'bsp'.
			Take it from the 'code' field of the depositor, never from 'id' or 'name'.""";

	private final DepositorService depositorService;

	DepositorCodes(DepositorService depositorService) {
		this.depositorService = depositorService;
	}

	String require(String depositorCode) {
		Depositor depositor = depositorService.findByCode(depositorCode);

		if (depositor != null) {
			return depositor.code();
		}

		throw new IllegalArgumentException(
				"Unknown depositor code: '" + depositorCode + "'. Use the exact code as stored, "
						+ "never the depositor name. Registered codes: "
						+ String.join(", ", depositorService.codes()) + ".");
	}
}