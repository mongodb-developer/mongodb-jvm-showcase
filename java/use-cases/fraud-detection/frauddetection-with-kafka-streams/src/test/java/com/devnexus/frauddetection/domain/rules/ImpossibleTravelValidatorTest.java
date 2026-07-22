package com.devnexus.frauddetection.domain.rules;

import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ImpossibleTravelValidatorTest {

    private ImpossibleTravelValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ImpossibleTravelValidator(900.0);
    }

    @Test
    void shouldDetectImpossibleTravel_SaoPauloToNewYork_In1Hour() {
        Transaction saoPaulo = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("100.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -23.5505, -46.6333
        );

        Transaction newYork = new Transaction(
                "tx2", "user1", "Apple Store", "New York",
                new BigDecimal("200.00"),
                Instant.parse("2024-01-15T11:00:00Z"), "1234-5678-9012-3456",
                40.7128, -74.0060
        );

        Optional<SuspiciousAlert> result = validator.validate(saoPaulo, newYork);

        assertTrue(result.isPresent());
        assertEquals("IMPOSSIBLE_TRAVEL", result.get().ruleId());
        assertTrue(result.get().description().contains("Sao Paulo to New York"));
    }

    @Test
    void shouldAllowTravel_SaoPauloToNewYork_In12Hours() {
        Transaction saoPaulo = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("100.00"),
                Instant.parse("2024-01-15T06:00:00Z"), "1234-5678-9012-3456",
                -23.5505, -46.6333
        );

        Transaction newYork = new Transaction(
                "tx2", "user1", "Apple Store", "New York",
                new BigDecimal("200.00"),
                Instant.parse("2024-01-15T18:00:00Z"), "1234-5678-9012-3456",
                40.7128, -74.0060
        );

        assertTrue(validator.validate(saoPaulo, newYork).isEmpty());
    }

    @Test
    void shouldAllowTravel_SameCity_ShortTime() {
        Transaction downtown = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("50.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -23.5489, -46.6388
        );

        Transaction paulista = new Transaction(
                "tx2", "user1", "McDonalds", "Sao Paulo",
                new BigDecimal("75.00"),
                Instant.parse("2024-01-15T10:30:00Z"), "1234-5678-9012-3456",
                -23.5632, -46.6542
        );

        assertTrue(validator.validate(downtown, paulista).isEmpty());
    }

    @Test
    void shouldDetectImpossibleTravel_SameTimeDistantLocations() {
        Transaction saoPaulo = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("100.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -23.5505, -46.6333
        );

        Transaction rioDeJaneiro = new Transaction(
                "tx2", "user1", "Starbucks", "Rio de Janeiro",
                new BigDecimal("200.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -22.9068, -43.1729
        );

        Optional<SuspiciousAlert> result = validator.validate(saoPaulo, rioDeJaneiro);

        assertTrue(result.isPresent());
        assertEquals("IMPOSSIBLE_TRAVEL", result.get().ruleId());
    }

    @Test
    void shouldReturnEmpty_WhenCoordinatesAreMissing() {
        Transaction withCoords = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("100.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -23.5505, -46.6333
        );

        Transaction withoutCoords = new Transaction(
                "tx2", "user1", "Unknown", "Unknown",
                new BigDecimal("200.00"),
                Instant.parse("2024-01-15T11:00:00Z"), "1234-5678-9012-3456",
                null, null
        );

        assertTrue(validator.validate(withCoords, withoutCoords).isEmpty());
    }

    @Test
    void shouldAllowTravel_SaoPauloToRio_In1Hour() {
        Transaction saoPaulo = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("100.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -23.5505, -46.6333
        );

        Transaction rio = new Transaction(
                "tx2", "user1", "Starbucks", "Rio de Janeiro",
                new BigDecimal("200.00"),
                Instant.parse("2024-01-15T11:00:00Z"), "1234-5678-9012-3456",
                -22.9068, -43.1729
        );

        assertTrue(validator.validate(saoPaulo, rio).isEmpty(),
                "360 km in 1 hour (360 km/h) should be possible");
    }

    @Test
    void shouldDetectImpossibleTravel_SaoPauloToRio_In10Minutes() {
        Transaction saoPaulo = new Transaction(
                "tx1", "user1", "Starbucks", "Sao Paulo",
                new BigDecimal("100.00"),
                Instant.parse("2024-01-15T10:00:00Z"), "1234-5678-9012-3456",
                -23.5505, -46.6333
        );

        Transaction rio = new Transaction(
                "tx2", "user1", "Starbucks", "Rio de Janeiro",
                new BigDecimal("200.00"),
                Instant.parse("2024-01-15T10:10:00Z"), "1234-5678-9012-3456",
                -22.9068, -43.1729
        );

        Optional<SuspiciousAlert> result = validator.validate(saoPaulo, rio);

        assertTrue(result.isPresent(),
                "360 km in 10 minutes (2160 km/h) should be impossible");
        assertEquals("IMPOSSIBLE_TRAVEL", result.get().ruleId());
    }
}