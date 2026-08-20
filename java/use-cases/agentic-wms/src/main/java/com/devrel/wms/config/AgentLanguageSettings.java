package com.devrel.wms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class AgentLanguageSettings {

	private final Logger logger = LoggerFactory.getLogger(AgentLanguageSettings.class);
	private final AtomicReference<AgentLanguage> language;

	AgentLanguageSettings(@Value("${wms.agent.language:EN}") AgentLanguage language) {
		this.language = new AtomicReference<>(language);
	}

	public AgentLanguage language() {
		return language.get();
	}

	public void change(AgentLanguage language) {
		this.language.set(language);

		logger.info("Agent language changed to {}", language);
	}

	public String instruction() {
		return "Always answer in " + language.get().description() + ".";
	}
}
