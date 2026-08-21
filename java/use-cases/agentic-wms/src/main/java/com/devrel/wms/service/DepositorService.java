package com.devrel.wms.service;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.DepositorRef;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.DepositorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositorService {

	private final Logger logger = LoggerFactory.getLogger(DepositorService.class);
	private final DepositorRepository depositorRepository;

	DepositorService(DepositorRepository depositorRepository) {
		this.depositorRepository = depositorRepository;
	}

	public Depositor save(Depositor depositor) {
		validate(depositor);

		Depositor saved = depositorRepository.save(depositor);

		logger.info("Depositor {} saved", saved.id());

		return saved;
	}

	public List<Depositor> findAll() {
		return depositorRepository.findAll();
	}

	public Depositor findById(String id) {
		return depositorRepository.findById(id).orElse(null);
	}

	public Depositor findByCode(String code) {
		return depositorRepository.findByCode(code).orElse(null);
	}

	public DepositorRef toRef(DepositorRef reference) {
		if (reference == null) {
			throw new IllegalArgumentException("Depositor is required");
		}

		Depositor depositor = reference.id() == null
				? depositorRepository.findByCode(reference.code()).orElse(null)
				: depositorRepository.findById(reference.id()).orElse(null);

		if (depositor == null) {
			throw new NotFoundException("Depositor not found: "
					+ (reference.id() == null ? reference.code() : reference.id()));
		}

		return new DepositorRef(depositor.id(), depositor.code(), depositor.name());
	}

	public Depositor update(String id, Depositor depositor) {
		Depositor current = depositorRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Depositor not found: " + id));

		Depositor merged = new Depositor(
				current.id(),
				current.code(),
				depositor.name() == null ? current.name() : depositor.name(),
				depositor.email() == null ? current.email() : depositor.email());

		validate(merged);

		Depositor saved = depositorRepository.save(merged);

		logger.info("Depositor {} updated", saved.id());

		return saved;
	}

	private void validate(Depositor depositor) {
		if (depositor.code() == null || depositor.code().isBlank()) {
			throw new IllegalArgumentException("Depositor code is required");
		}

		if (depositor.name() == null || depositor.name().isBlank()) {
			throw new IllegalArgumentException("Depositor name is required");
		}

		if (depositor.email() == null || depositor.email().isBlank()) {
			throw new IllegalArgumentException("Depositor email is required");
		}
	}
}
