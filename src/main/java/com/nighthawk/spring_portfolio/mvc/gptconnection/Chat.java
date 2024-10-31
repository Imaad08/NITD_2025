package com.nighthawk.spring_portfolio.mvc.gptconnection;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class Chat {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";  // or "gpt-4" if using GPT-4

    public String getGptScore(String code) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // Construct JSON request body
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", MODEL);

        // Construct messages array
        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", "Please grade this code and provide only a numeric score (0-100): " + code);

        requestBody.putArray("messages").add(userMessage);
        requestBody.put("temperature", 0.0);

        // Create request
        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonNode jsonNode = mapper.readTree(response.body().string());
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            } else {
                System.err.println("Request failed: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed to get a score from GPT";
    }
}