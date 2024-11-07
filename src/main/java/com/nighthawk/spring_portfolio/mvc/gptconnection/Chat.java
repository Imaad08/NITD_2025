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
    private static final String MODEL = "gpt-3.5-turbo"; 

    public String getGptScore(String code) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // Construct JSON request body
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", MODEL);

        // Construct messages array
        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", "Please grade this code/answer and provide only a numeric score (0-1000) with this rubric Correctness and Completeness (500 points): 500 - completely correct, 450 - minor issues or unhandled edge cases, 400 - several small errors, 350 - partial with multiple issues, below 300 - major issues/incomplete; Efficiency and Optimization (200 points): 200 - optimal or near-optimal, 180 - minor optimization needed, 160 - functional but inefficient, 140 - improvements needed, below 140 - inefficient; Code Structure and Organization (150 points): 150 - well-organized, 130 - mostly organized, 110 - readable but lacks structure, 90 - hard to follow, below 90 - unorganized; Readability and Documentation (100 points): 100 - clear, well-documented, 85 - readable but limited comments, 70 - somewhat readable, 50 - minimally readable, below 50 - poor readability; Error Handling and Edge Cases (50 points): 50 - handles all cases, 40 - most cases covered, 30 - some cases covered, 20 - minimal handling, below 20 - little attention; Extra Credit (100 points): impressive/innovative elements. Give me a integer score from 1-1000 and only an integer no text around it. Again. Only a integer with no other text 1-1000" + code);

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