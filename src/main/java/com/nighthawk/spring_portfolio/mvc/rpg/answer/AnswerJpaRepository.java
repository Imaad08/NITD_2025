package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nighthawk.spring_portfolio.mvc.rpg.player.Player;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long question);
    Optional<Answer> findByQuestionAndPlayer(Question question, Player player);
}
