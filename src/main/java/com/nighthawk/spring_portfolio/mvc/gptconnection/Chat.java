package com.nighthawk.spring_portfolio.mvc.gptconnection;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;
import okhttp3.MediaType;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class Chat {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${thread.id}")
    private String threadId;

    @Value("${assistant.id}")
    private String assistantId;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4";

    // Method to request a score from GPT-4
    public String getGptScore(String code) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // JSON body with instruction for numeric score only
        String jsonBody = String.format(
                "{ \"model\": \"%s\", " +
                "\"messages\": [" +
                "{\"role\": \"user\", \"content\": \"Please grade this code and provide only a numeric score (0-100): %s\"}," +
                "{\"role\": \"assistant\", \"content\": \"Only provide a score as a number.\", \"id\": \"%s\"}]," +
                "\"temperature\": 0.0, \"thread_id\": \"%s\" }",
                MODEL, code, assistantId, threadId
        );

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JsonNode jsonNode = mapper.readTree(response.body().string());
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed to get a score from GPT";
    }
}