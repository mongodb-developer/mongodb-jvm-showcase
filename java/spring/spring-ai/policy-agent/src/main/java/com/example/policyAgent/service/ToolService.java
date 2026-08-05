package com.example.policyAgent.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ToolService {


	@Tool(description = "Get the current date")
	public Date getDate() {
		return new Date();
	}

}
