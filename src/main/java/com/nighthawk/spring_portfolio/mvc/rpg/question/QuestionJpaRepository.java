package com.nighthawk.spring_portfolio.mvc.rpg.question;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {
    Question findByTitle(String title); 
}

