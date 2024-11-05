package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nighthawk.spring_portfolio.mvc.rpg.player.Player;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long question);
    Optional<Answer> findByQuestionAndPlayer(Question question, Player player);

    @Query("SELECT p.name, SUM(a.chatScore) AS totalScore FROM Answer a JOIN a.player p GROUP BY p.name ORDER BY totalScore DESC LIMIT 10")
    List<Object[]> findTop10PlayersByTotalScore();
}
