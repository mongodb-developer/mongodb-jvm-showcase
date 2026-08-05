package com.example.policyAgent.service;

import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	public int count(String message) {
		JTokkitTokenCountEstimator jTokkitTokenCountEstimator = new JTokkitTokenCountEstimator();
		return jTokkitTokenCountEstimator.estimate(message);
	}
}
