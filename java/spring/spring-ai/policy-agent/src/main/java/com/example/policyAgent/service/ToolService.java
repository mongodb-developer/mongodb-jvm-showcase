package com.example.policyAgent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ToolService {

	private final Logger logger = LoggerFactory.getLogger(ToolService.class);

	@Tool(description = "Get the current date")
	public Date getDate() {
		logger.info("Getting Tool current date");
		return new Date();
	}

}
