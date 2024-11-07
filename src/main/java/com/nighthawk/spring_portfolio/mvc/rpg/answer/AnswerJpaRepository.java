package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long question);
    List<Answer> findByPlayerId(Integer playerid);
}
