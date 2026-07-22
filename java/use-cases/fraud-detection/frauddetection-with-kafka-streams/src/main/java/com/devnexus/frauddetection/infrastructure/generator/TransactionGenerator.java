package com.devnexus.frauddetection.infrastructure.generator;

import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.infrastructure.message.producer.TransactionProducer;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "app.generator.enabled", havingValue = "true")
public class TransactionGenerator {

    private static final Logger log = LoggerFactory.getLogger(TransactionGenerator.class);

    private static final double P_BIG_SPEND = 0.03;
    private static final double P_IMPOSSIBLE_TRAVEL = 0.02;
    private static final double P_DUPLICATE_SEND = 0.02;
    private static final double P_RANDOM_MERCHANT = 0.05;

    private static final List<CustomerProfile> CUSTOMERS = List.of(
            new CustomerProfile("USR-001", "4111-1111-1111-1111", 24.50, 6.00, "US"),
            new CustomerProfile("USR-002", "5555-2222-3333-4444", 120.00, 35.00, "GB"),
            new CustomerProfile("USR-003", "3782-822463-10005", 9000.00, 2500.00, "JP")
    );

    private static final List<City> CITIES = List.of(
            new City("Seattle", "US", 47.6101, -122.2015, List.of("Starbucks", "REI", "Target")),
            new City("Austin", "US", 30.2672, -97.7431, List.of("Whole Foods", "Trader Joe's", "H-E-B")),
            new City("London", "GB", 51.5072, -0.1276, List.of("Tesco", "Harrods", "Marks & Spencer")),
            new City("Manchester", "GB", 53.4808, -2.2426, List.of("Tesco", "Boots", "Pret A Manger")),
            new City("Tokyo", "JP", 35.6762, 139.6503, List.of("Yodobashi Camera", "FamilyMart", "Uniqlo")),
            new City("Osaka", "JP", 34.6937, 135.5023, List.of("Lawson", "Bic Camera", "Don Quijote"))
    );

    // “Random merchant” pool (intentionally generic)
    private static final List<String> RANDOM_MERCHANTS = List.of(
            "ATM Withdrawal", "Luxury Boutique", "Crypto Exchange", "Online Electronics",
            "Airline Tickets", "Hotel Booking", "Car Rental", "Jewellery Store", "Late-Night Delivery",
            "Grocery Store", "Pharmacy", "Gas Station", "Fast Food Restaurant", "Bookstore"
    );

    private final TransactionProducer producer;
    private final Random random = new Random();
    private final long intervalMs;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "transaction-generator");
                t.setDaemon(true);
                return t;
            });

    private int nextCustomerIndex = 0;

    public TransactionGenerator(
            TransactionProducer producer,
            @Value("${app.generator.interval-ms:1000}") long intervalMs
    ) {
        this.producer = producer;
        this.intervalMs = intervalMs;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        scheduler.scheduleAtFixedRate(this::safeGenerate, 0, intervalMs, TimeUnit.MILLISECONDS);
        log.info("Transaction generator started interval={}ms", intervalMs);
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdownNow();
    }

    private void safeGenerate() {
        try {
            generateTransaction();
        } catch (Exception e) {
            log.warn("Transaction generator failed", e);
        }
    }

    private void generateTransaction() {
        CustomerProfile customer = CUSTOMERS.get(nextCustomerIndex);
        nextCustomerIndex = (nextCustomerIndex + 1) % CUSTOMERS.size();

        boolean impossibleTravel = chance(P_IMPOSSIBLE_TRAVEL);
        boolean bigSpend = chance(P_BIG_SPEND);
        boolean randomMerchant = chance(P_RANDOM_MERCHANT);
        boolean duplicateSend = chance(P_DUPLICATE_SEND);

        City city = impossibleTravel
                ? pickForeignCity(customer.preferredCountry())
                : pickHomeCity(customer.preferredCountry());

        String merchant = randomMerchant
                ? RANDOM_MERCHANTS.get(random.nextInt(RANDOM_MERCHANTS.size()))
                : pickMerchant(city);

        BigDecimal amount = bigSpend
                ? generateBigSpend(customer)
                : generateNormalAmount(customer);

        Instant when = Instant.now();

        Transaction tx = new Transaction(
                "TXN-" + UUID.randomUUID(),
                customer.userId(),
                merchant,
                city.name(),
                amount,
                when,
                customer.cardNumber(),
                city.latitude(),
                city.longitude()
        );

        producer.send(tx);

        if (duplicateSend) {
            producer.send(tx);
            producer.send(tx);
            producer.send(tx);
        }

        log.debug("Generated txId={} userId={} amount={} city={} merchant={} flags=[travel={},big={},dup={},randMerchant={}]",
                tx.transactionId(), tx.userId(), tx.transactionAmount(), tx.city(), tx.merchant(),
                impossibleTravel, bigSpend, duplicateSend, randomMerchant);
    }

    private BigDecimal generateNormalAmount(CustomerProfile customer) {
        double raw = customer.mean() + customer.stdDev() * random.nextGaussian();
        double normalized = Math.max(raw, 1.0);
        return BigDecimal.valueOf(normalized).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal generateBigSpend(CustomerProfile customer) {
        double multiplier = 20 + random.nextInt(81);
        double raw = Math.max(customer.mean() * multiplier, 500.0);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }

    private City pickHomeCity(String preferredCountry) {
        List<City> local = CITIES.stream()
                .filter(c -> preferredCountry.equals(c.countryCode()))
                .toList();
        return local.get(random.nextInt(local.size()));
    }

    private City pickForeignCity(String preferredCountry) {
        List<City> foreign = CITIES.stream()
                .filter(c -> !preferredCountry.equals(c.countryCode()))
                .toList();
        return foreign.get(random.nextInt(foreign.size()));
    }

    private String pickMerchant(City city) {
        List<String> merchants = city.merchants();
        return merchants.isEmpty() ? "Merchant" : merchants.get(random.nextInt(merchants.size()));
    }

    private boolean chance(double p) {
        return random.nextDouble() < p;
    }

    private record CustomerProfile(
            String userId,
            String cardNumber,
            double mean,
            double stdDev,
            String preferredCountry
    ) {}

    private record City(
            String name,
            String countryCode,
            double latitude,
            double longitude,
            List<String> merchants
    ) {}
}
