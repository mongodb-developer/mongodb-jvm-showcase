package com.example.policyAgent.service;

import com.example.policyAgent.model.Plan;
import com.example.policyAgent.repository.PlanRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

	private final PlanRepository planRepository;

	PlanService(PlanRepository planRepository) {
		this.planRepository = planRepository;
	}

	public Plan create(Plan plan) {
		return planRepository.save(plan);
	}

	public Plan findById(String id) {
		return planRepository.findById(id).orElseThrow();
	}

	public void executeTask(String task) {

	}
}
