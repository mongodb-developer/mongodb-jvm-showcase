package com.example.policyAgent.service;

import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private final JTokkitTokenCountEstimator tokenCountEstimator = new JTokkitTokenCountEstimator();

	public int count(String message) {
		return tokenCountEstimator.estimate(message);
	}
}
