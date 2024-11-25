package org.example.sda_frontend.ai;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.example.sda_frontend.currency.Owning;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;

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

    public String getSingleCoinOverview(String code) {
        try {
            HttpResponse<String> response = Unirest.post("https://api.livecoinwatch.com/overview")
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .body("{\n\t\"currency\": "+code+"\n}")
                    .asString();

            JSONObject coin = new JSONObject(response.getBody());

            if (coin.has("error")) {
                JSONObject error = coin.getJSONObject("error");
                int errorCode = error.getInt("code");
                String status = error.getString("status");
                String description = error.getString("description");

                return "";
            }

            // Extract the data as required
            String name = coin.getString("cap");
            double rate = coin.getDouble("volume");
            long volume = coin.getLong("liquidity");
            long cap = coin.getLong("btcDominance");

            // Return extracted values
            return "Name: " + name + "\nCode: " + code + "\nRate: " + rate + "\nVolume: " + volume + "\nMarket Cap: " + cap;
        } catch (UnirestException e) {
            System.err.println("An error occurred while making the API request: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private String getValueOrNA(JSONObject json, String key) {
        try {
            Object value = json.get(key);
            return value == null || value == JSONObject.NULL ? "N/A" : value.toString();
        } catch (JSONException e) {
            return "N/A";
        }
    }

    public String getSpecificCoinDetails(JSONObject coin, int i, boolean single, String coinCode) {
        StringBuilder overviewBuilder = new StringBuilder();

        if (single) {
            overviewBuilder.append("coin" + i + coinCode).append(coinCode);
        } else {
            overviewBuilder.append("coin" + i + " code:").append(coin.getString("code"));
        }

        overviewBuilder.append(", rank:").append(getValueOrNA(coin, "rank"))
                .append(", exchanges: ").append(getValueOrNA(coin, "exchanges"))
                .append(", markets: ").append(getValueOrNA(coin, "markets"))
                .append(", volume: ").append(getValueOrNA(coin, "volume"))
                .append(", cap: ").append(getValueOrNA(coin, "cap"))
                .append(", rate: ").append(getValueOrNA(coin, "rate"))
                .append(", allTimeHighUSD: ").append(getValueOrNA(coin, "allTimeHighUSD"));

        JSONObject delta = coin.optJSONObject("delta");
        overviewBuilder.append(", hour: ").append(delta != null ? getValueOrNA(delta, "hour") : "N/A")
                .append(", day: ").append(delta != null ? getValueOrNA(delta, "day") : "N/A")
                .append(", week: ").append(delta != null ? getValueOrNA(delta, "week") : "N/A")
                .append(", month: ").append(delta != null ? getValueOrNA(delta, "month") : "N/A")
                .append(", quarter: ").append(delta != null ? getValueOrNA(delta, "quarter") : "N/A")
                .append(", year: ").append(delta != null ? getValueOrNA(delta, "year") : "N/A")
                .append(", circulatingSupply: ").append(getValueOrNA(coin, "circulatingSupply"))
                .append(", totalSupply: ").append(getValueOrNA(coin, "totalSupply"))
                .append(", maxSupply: ").append(getValueOrNA(coin, "maxSupply"))
                .append(", age: ").append(getValueOrNA(coin, "age"));

        JSONArray categories = coin.getJSONArray("categories");
        overviewBuilder.append(", categories: ").append(categories.toString());

        JSONObject links = coin.getJSONObject("links");
        overviewBuilder.append(", links: ").append(links.toString());

        overviewBuilder.append(". ");

        return overviewBuilder.toString();
    }

    public String getCoinsForAI(int size, List<Owning> ownings) {
        // Get top coins from giveTopCoins method
        JSONArray topCoins = giveTopCoins(size);
        String overview = "";

        // Add top coins to the overview
        for (int i = 0; i < topCoins.length(); i++) {
            overview += getSpecificCoinDetails(topCoins.getJSONObject(i), i, false, "");
        }

        int length = topCoins.length();

        // Check if owned coins are in the top coins list, if not, add them
        for (Owning owning : ownings) {
            boolean isCoinInTop = false;
            for (int i = 0; i < topCoins.length(); i++) {
                if (topCoins.getJSONObject(i).getString("code").equals(owning.getCoin())) {
                    isCoinInTop = true;
                    break;
                }
            }
            if (!isCoinInTop) {
                overview += getSpecificCoinDetails(giveSingleCoin(owning.getCoin()), length, true, owning.getCoin());
                ++length;
            }
        }

        return overview;
    }
}
