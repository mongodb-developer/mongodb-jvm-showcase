package com.devrel.wms.service;

import com.devrel.wms.entity.Replenishment;
import com.devrel.wms.repository.ReplenishmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplenishmentService {

	private final Logger logger = LoggerFactory.getLogger(ReplenishmentService.class);
	ReplenishmentRepository replenishmentRepository;

	ReplenishmentService(ReplenishmentRepository replenishmentRepository) {
		this.replenishmentRepository = replenishmentRepository;
	}

	public Replenishment save(Replenishment replenishment) {
		Replenishment save = replenishmentRepository.save(replenishment);

		logger.info("Replenishment with Id {} saved", save.id());

		return save;
	}

	public List<Replenishment> findAll() {
		return replenishmentRepository.findAll();
	}

	public Replenishment findById(String id) {
		return replenishmentRepository.findById(id).orElse(null);
	}
}
