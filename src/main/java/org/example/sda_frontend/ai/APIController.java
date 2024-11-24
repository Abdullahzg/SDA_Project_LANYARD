package org.example.sda_frontend.ai;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIController {
    private String apiKey;

    public APIController(String api) {
        this.apiKey = api;

    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    public String getApiKey() {
        return apiKey;
    }

    public void printTopCoins(int top){
        try {
            HttpResponse<String> response = Unirest.post("https://api.livecoinwatch.com/coins/list")
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .body("{\n\t\"currency\": \"USDT\",\n\t\"sort\": \"rank\",\n\t\"order\": \"ascending\",\n\t\"offset\": 0,\n\t\"limit\": "+top+",\n\t\"meta\": false\n}")
                    .asString();
            JSONArray coinsArray = new JSONArray(response.getBody());

            // Iterate through each coin object in the array
            for (int i = 0; i < coinsArray.length(); i++) {
                JSONObject coin = coinsArray.getJSONObject(i);

                // Extract the data as required
                String code = coin.getString("code");
                double rate = coin.getDouble("rate");
                long volume = coin.getLong("volume");
                long cap = coin.getLong("cap");



                // Print extracted values
                System.out.println("Code: " + code);
                System.out.println("Rate: " + rate);
                System.out.println("Volume: " + volume);
                System.out.println("Market Cap: " + cap);
                System.out.println("----------------------------");
            }
            // Print the response
            System.out.println("Response: " + response.getBody());
        } catch (UnirestException e) {
            System.err.println("An error occurred while making the API request: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public JSONArray giveTopCoins(int top){
        try {
            HttpResponse<String> response = Unirest.post("https://api.livecoinwatch.com/coins/list")
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .body("{\n\t\"currency\": \"USDT\",\n\t\"sort\": \"rank\",\n\t\"order\": \"ascending\",\n\t\"offset\": 0,\n\t\"limit\": "+top+",\n\t\"meta\": true\n}")
                    .asString();
            JSONArray coinsArray = new JSONArray(response.getBody());

            return coinsArray;
        } catch (UnirestException e) {
            System.err.println("An error occurred while making the API request: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void printSingleCoin(String code) {
        try {
            HttpResponse<String> response = Unirest.post("https://api.livecoinwatch.com/coins/single")
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .body("{\n\t\"currency\": \"USD\",\n\t\"code\": \"" + code + "\",\n\t\"meta\": true\n}")
                    .asString();

            JSONObject coin = new JSONObject(response.getBody());

            if (coin.has("error")) {
                JSONObject error = coin.getJSONObject("error");
                int errorCode = error.getInt("code");
                String status = error.getString("status");
                String description = error.getString("description");

                System.out.println("Error Code: " + errorCode + " || Status: " + status);
                System.out.println("Description: " + description);
                System.out.println("The coin with code \"" + code + "\" does not exist.");
                return;
            }

            // Extract the data as required
            String name = coin.getString("name");
            double rate = coin.getDouble("rate");
            long volume = coin.getLong("volume");
            long cap = coin.getLong("cap");

            // Print extracted values
            System.out.println("Name: " + name);
            System.out.println("Code: " + code);
            System.out.println("Rate: " + rate);
            System.out.println("Volume: " + volume);
            System.out.println("Market Cap: " + cap);
            System.out.println("----------------------------");

            // Print the response
            System.out.println("Response: " + response.getBody());
        } catch (UnirestException e) {
            System.err.println("An error occurred while making the API request: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public JSONObject giveSingleCoin(String code) {
        try {
            HttpResponse<String> response = Unirest.post("https://api.livecoinwatch.com/coins/single")
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .body("{\n\t\"currency\": \"USD\",\n\t\"code\": \"" + code + "\",\n\t\"meta\": true\n}")
                    .asString();

            JSONObject coin = new JSONObject(response.getBody());
            System.out.println(coin);
            return coin;

        } catch (UnirestException e) {
            System.err.println("An error occurred while making the API request: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public float getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            HttpResponse<String> response = Unirest.post("https://api.livecoinwatch.com/coins/single")
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .body("{\n\t\"currency\": \"" + toCurrency + "\",\n\t\"code\": \"" + fromCurrency + "\",\n\t\"meta\": false\n}")
                    .asString();

            JSONObject coin = new JSONObject(response.getBody());
            if (coin.has("rate")) {
                return (float) coin.getDouble("rate");
            } else {
                System.err.println("Exchange rate not found for " + fromCurrency + " to " + toCurrency);
                return -1;
            }
        } catch (UnirestException e) {
            System.err.println("Error while fetching exchange rate: " + e.getMessage());
            return -1; // Return an invalid value for error handling
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return -1; // Return an invalid value for error handling
        }
    }
}
