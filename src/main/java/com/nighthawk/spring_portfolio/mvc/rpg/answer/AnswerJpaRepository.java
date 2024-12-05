package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long question);
    List<Answer> findByUserId(Integer userid);

    @Query("SELECT NEW com.nighthawk.spring_portfolio.mvc.rpg.answer.LeaderboardDto(p.id, SUM(a.chatScore)) FROM Answer a JOIN a.player p GROUP BY p.id ORDER BY SUM(a.chatScore) DESC")
    List<LeaderboardDto> findTop10PlayersByTotalScore();
}
