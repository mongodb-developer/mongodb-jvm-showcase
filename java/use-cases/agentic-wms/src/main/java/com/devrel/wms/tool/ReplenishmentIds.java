package com.devrel.wms.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ReplenishmentIds {

	public static final String ID_PARAM = "Id of the replenishment request, exactly as returned by "
			+ "the creation tool. Use only the 24 character hexadecimal value, never prefixed by a "
			+ "word such as 'Replenishment'";

	private static final Pattern ID = Pattern.compile("[0-9a-fA-F]{24}");

	public static String sanitize(String value) {
		if (value == null) {
			return null;
		}

		Matcher matcher = ID.matcher(value);

		return matcher.find() ? matcher.group() : value.trim();
	}

	private ReplenishmentIds() {
	}
}
