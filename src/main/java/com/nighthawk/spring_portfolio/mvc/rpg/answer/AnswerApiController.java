package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nighthawk.spring_portfolio.mvc.rpg.player.PlayerJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;
import com.nighthawk.spring_portfolio.mvc.rpg.question.QuestionJpaRepository;
import com.nighthawk.spring_portfolio.mvc.stocks.User;
import com.nighthawk.spring_portfolio.mvc.stocks.UserJpaRepository;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
@RestController
@RequestMapping("/rpg_answer")
public class AnswerApiController {

    // Load environment variables using dotenv
    private final Dotenv dotenv = Dotenv.load();
    private final String apiUrl = dotenv.get("API_URL");
    private final String apiKey = dotenv.get("API_KEY");

    @Autowired
    private AnswerJpaRepository answerJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;
    





    @Getter 
    public static class AnswerDto {
        private String content;
        private Long questionId;
        private Long userId;
        private Long chatScore; 
    }


    @GetMapping("/getChatScore/{userid}")
    public ResponseEntity<Long> getChatScore(@PathVariable Integer userid) {
        List<Answer> useranswers = answerJpaRepository.findByUserId(userid);
        Long totalChatScore = 0L;

        for (Answer useranswer : useranswers) {
            Long questionChatScore = useranswer.getChatScore();
            totalChatScore += questionChatScore;
        }

        return new ResponseEntity<>(totalChatScore, HttpStatus.OK);

    }

    @GetMapping("/getBalance/{userid}") 
    public ResponseEntity<Double> getBalance(@PathVariable Integer userid) {
        User userOpt = userJpaRepository.findById(userid);
        
        Double balance = userOpt.getBalance();

        return new ResponseEntity<>(balance, HttpStatus.OK);

    }

    @PostMapping("/submitAnswer")
    public ResponseEntity<Answer> postAnswer(@RequestBody AnswerDto answerDto) {
        Optional<Question> questionOpt = questionJpaRepository.findById(answerDto.getQuestionId());
        Optional<User> userOpt = userJpaRepository.findById(answerDto.getUserId());

        System.out.println("API Key: " + apiKey);

        if (questionOpt.isEmpty() || userOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Question question = questionOpt.get();
        User user = userOpt.get();

        System.out.println(user);
        System.out.println(question);

        String rubric = "Provide a score from 1 to 10 evaluating the clarity, completeness, "
                        + "and relevance of the following response in relation to the question asked:";

        Long chatScore = getChatScore(answerDto.getContent(), rubric);

        Answer answer = new Answer(answerDto.getContent(), question, user, chatScore);
        answerJpaRepository.save(answer);

        // updateBalance
        double questionPoints = question.getPoints();
        user.setBalance(user.getBalance() + questionPoints);
        userJpaRepository.save(user);

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    private Long getChatScore(String content, String rubric) {
        try {
            String requestBody = "{ \"model\": \"gpt-3.5-turbo\", \"messages\": ["
                                 + "{\"role\": \"system\", \"content\": \"" + rubric + "\"},"
                                 + "{\"role\": \"user\", \"content\": \"" + content + "\"} ] }";
    
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
    
            if (response.statusCode() == HttpStatus.OK.value()) {
                return parseScoreFromResponse(response.body());
            } else {
                // Log error response
                System.out.println("Error: " + response.body());
                return 0L; 
            }
    
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); 
            return 0L;  
        }
    }

        

    private Long parseScoreFromResponse(String responseBody) {
        try {
            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();
        
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            String content = rootNode.path("choices").get(0).path("message").path("content").asText();

            if (content.contains("relevant")) {
                return 10L;
            } else if (content.contains("clarification")) {
                return 8L;
            } else {
                return 5L;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /*
    @GetMapping("/leaderboard")
    public List<LeaderboardDto> getLeaderboard() {
    // Directly get List<LeaderboardDto> from repository without casting to Object[]
    List<LeaderboardDto> leaderboardEntries = answerJpaRepository.findTop10PlayersByTotalScore();

    // Loop to populate player names if needed
    for (LeaderboardDto entry : leaderboardEntries) {
        Optional<Player> player = playerJpaRepository.findById(entry.getId());
        String playerName = player.isPresent() ? player.get().getName() : "Unknown";
        entry.setPlayerName(playerName);
    }

    return leaderboardEntries;
}
*/


}
