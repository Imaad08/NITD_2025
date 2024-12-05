package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;
import com.nighthawk.spring_portfolio.mvc.stocks.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // add date 
    private Long chatScore;

    public Answer (String content, Question question, User user, Long chatScore) {
        this.content = content;
        this.question = question;
        this.user = user;
        this.chatScore = chatScore;
    }

}
