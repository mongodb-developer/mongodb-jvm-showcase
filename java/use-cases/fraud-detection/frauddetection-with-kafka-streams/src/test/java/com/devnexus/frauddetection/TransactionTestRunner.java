package com.devnexus.frauddetection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

public class TransactionTestRunner {

    private static final String URL = "http://localhost:8081/api/transactions";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String CARD = "4532-0000-0000-1234";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Impossible Travel Test ===\n");

        sendTransaction("TXN-IT-1", "USR-001", "Starbucks", "Sao Paulo",
                50.00, Instant.now().toString(), CARD, -23.5505, -46.6333);
        Thread.sleep(500);

        sendTransaction("TXN-IT-2", "USR-001", "Apple Store", "New York",
                200.00, Instant.now().toString(), CARD, 40.7128, -74.0060);
        Thread.sleep(1000);

        System.out.println("\n=== Velocity Check Test (4 rapid transactions) ===\n");

        String velocityCard = "4532-0000-0000-5678";
        for (int i = 1; i <= 4; i++) {
            sendTransaction("TXN-VEL-" + i, "USR-002", "Store " + i, "Miami",
                    10.00 * i, Instant.now().toString(), velocityCard, 25.7617, -80.1918);
            Thread.sleep(200);
        }

        System.out.println("\nDone!");
    }

    private static void sendTransaction(String id, String userId, String merchant,
                                         String city, double amount, String time,
                                         String cardNumber, double lat, double lon) throws Exception {
        String json = """
            {
                "transactionId": "%s",
                "userId": "%s",
                "merchant": "%s",
                "city": "%s",
                "transactionAmount": %.2f,
                "transactionTime": "%s",
                "cardNumber": "%s",
                "latitude": %f,
                "longitude": %f
            }
            """.formatted(id, userId, merchant, city, amount, time, cardNumber, lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.printf("%-12s | ID: %-15s | Card: %s | City: %-12s | Amount: %8.2f | Status: %d%n",
            merchant, id, cardNumber, city, amount, response.statusCode());
    }
}
