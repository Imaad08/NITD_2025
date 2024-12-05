package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import java.util.ArrayList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Streak is a POJO, Plain Old Java Object.
 * --- @Data is a Lombok annotation that generates @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor.
 * --- @AllArgsConstructor is a Lombok annotation for a constructor with all arguments.
 * --- @NoArgsConstructor is a Lombok annotation for a constructor with no arguments.
 * --- @Entity annotation is used to mark the class as a persistent Java class.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Streak {

    /** Automatic unique identifier for Streak record
     * --- @Id annotation specifies the identifier property of the entity.
     * --- @GeneratedValue annotation specifies the primary key generation strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** userId links to the user entity
     * --- @NotNull annotation ensures that the field cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private Long userId;

    /** currentStreak is the number of consecutive days the user has interacted
     * --- @Column annotation sets the default value and ensures it is not null.
     */
    @NotNull
    @Column(nullable = false, columnDefinition = "int default 0")
    private int currentStreak;

    /** maxStreak is the longest streak the user has achieved
     * --- @Column annotation sets the default value and ensures it is not null.
     */
    @NotNull
    @Column(nullable = false, columnDefinition = "int default 0")
    private int maxStreak;

    /** Custom constructor for creating a Streak object with specific details */
    public Streak(Long userId, int currentStreak, int maxStreak) {
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
    }

    /** Static method to create a new Streak instance */
    public static Streak createStreak(Long userId, int currentStreak, int maxStreak) {
        return new Streak(userId, currentStreak, maxStreak);
    }

    /** Static method to initialize an array of Streak objects for testing */
    public static Streak[] init() {
        ArrayList<Streak> streaks = new ArrayList<>();
        streaks.add(createStreak(1L, 5, 10));
        return streaks.toArray(new Streak[0]);
    }
}
