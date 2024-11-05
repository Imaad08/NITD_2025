package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.rpg.player.Player;
import com.nighthawk.spring_portfolio.mvc.rpg.player.PlayerJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;
import com.nighthawk.spring_portfolio.mvc.rpg.question.QuestionJpaRepository;

import lombok.Getter;


@RestController
@RequestMapping("/rpg_answer")
public class AnswerApiController {

    @Autowired
    private AnswerJpaRepository answerJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;
    @Getter 
    public static class AnswerDto {
        private String content;
        private Long questionId;
        private Long playerId;
        private Long chatScore; 
    }


    @PostMapping("/submitanswer") 
    public ResponseEntity<Answer> postAnswer(@RequestBody AnswerDto answerDto) {
        Optional<Question> questionOpt = questionJpaRepository.findById(answerDto.getQuestionId());
        Optional<Player> playerOpt = playerJpaRepository.findById(answerDto.getPlayerId());

        
        Question question = questionOpt.get();
        Player player = playerOpt.get();

        Answer answer = new Answer(answerDto.getContent(), question, player, answerDto.getChatScore());
        answerJpaRepository.save(answer);
        
        
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
    @Getter
    public static class LeaderboardDto {
        private String playerName;
        private int totalScore;
    }

    @Autowired
    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        List<Object[]> top10Players = answerRepository.findTop10PlayersByTotalScore();
        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();

        int rank = 1;
        for (Object[] playerData : top10Players) {
            String playerName = (String) playerData[0];
            Integer totalScore = (Integer) playerData[1];

            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setPlayerName(playerName);
            entry.setChatScore(totalScore);
            entry.setRank(rank);
            leaderboardEntries.add(entry);
            rank++;
        }

        return leaderboardEntries;
    }
}

    // @GetMapping("/leaderboard") 
    // public ResponseEntity<List<Player>> getLeaderboard() {

        // int badgeCount = answerJpaRepository.countByPlayer(player);
        /* Leaderboard:
        - Rank(1/2/3/4)
        - Player(Randall)
        - Points(1,000,000)
        - Questions Answered(12)
        - Badge Count(12)
        - Class(CSA, CSP, CSSE)
        - Average Chat Score(1-1000, 1000 being highest rating)
         */

        // Nice feature: for each question, can view the player with the highest chatScore
        // Able to view their answer 
        // return new ResponseEntity<>(leaderboard, HttpStatus.OK);
    //}

    
}
