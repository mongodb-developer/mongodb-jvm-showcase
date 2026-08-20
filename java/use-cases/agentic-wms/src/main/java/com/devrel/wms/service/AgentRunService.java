package com.devrel.wms.service;

import com.devrel.wms.domain.AgentRun;
import com.devrel.wms.repository.AgentRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentRunService {

	private final Logger logger = LoggerFactory.getLogger(AgentRunService.class);
	AgentRunRepository agentRunRepository;

	AgentRunService(AgentRunRepository agentRunRepository) {
		this.agentRunRepository = agentRunRepository;
	}

	public AgentRun save(AgentRun agentRun) {
		AgentRun save = agentRunRepository.save(agentRun);

		logger.info("AgentRun with Id {} saved", save.id());

		return save;
	}

	public List<AgentRun> findAll() {
		return agentRunRepository.findAllByOrderByStartedAtDesc();
	}

	public AgentRun findById(String id) {
		return agentRunRepository.findById(id).orElse(null);
	}

}
