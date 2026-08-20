package com.devrel.wms.config;

public enum AgentLanguage {

	EN("English", "US"),
	PT_BR("Brazilian Portuguese", "BR");

	private final String description;
	private final String flag;

	AgentLanguage(String description, String flag) {
		this.description = description;
		this.flag = flag;
	}

	public String description() {
		return description;
	}

	public String flag() {
		return flag;
	}
}
