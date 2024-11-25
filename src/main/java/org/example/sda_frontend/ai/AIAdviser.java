package org.example.sda_frontend.ai;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.sda_frontend.currency.Owning;
import org.example.sda_frontend.user.Customer;
import org.example.sda_frontend.wallet.FiatWallet;
import org.example.sda_frontend.wallet.SpotWallet;

public class AIAdviser {
    private final String GROQ_API_KEY;
    private final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public AIAdviser() {
        // Load API key from properties file
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("lanyard.properties")) {
            if (input == null) {
                throw new Exception("Unable to find lanyard.properties in resources folder");
            }
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.GROQ_API_KEY = props.getProperty("groq_lanyard");
        if (this.GROQ_API_KEY == null || this.GROQ_API_KEY.trim().isEmpty()) {
            throw new RuntimeException("groq_lanyard not found in lanyard.properties");
        } else {
            this.client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();
            this.mapper = new ObjectMapper();
        }
    }

    public String getAdvice(Customer customer, APIController api, String inPrompt) {
        String financialSummary = customer.getFinancialSummary(api);
        String topCoins = api.getCoinsForAI(20, customer.getFiatWallet().getOwnings());


        // Build the analysis prompt
        String prompt = String.format(
                "Here is my financial summary:\n%s\n" +
                        "Here is some market overview:\n%s\n" +
                        inPrompt+"\nDo not suggest USDT. Keep the response to 50 words. Start with \"I advise\".",
                financialSummary, topCoins
        );

        // Create request body
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "llama-3.1-70b-versatile");
        requestBody.putArray("messages")
                .add(mapper.createObjectNode()
                        .put("role", "user")
                        .put("content", prompt));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        // Send request and get response
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null) {
            // Parse and return the analysis
            ObjectNode jsonResponse = null;
            try {
                jsonResponse = mapper.readValue(response.body(), ObjectNode.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonResponse != null) {
                return jsonResponse.at("/choices/0/message/content").asText();
            }
        }

        return null;
    }
}