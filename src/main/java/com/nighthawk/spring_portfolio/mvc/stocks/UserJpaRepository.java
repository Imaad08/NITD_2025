package com.nighthawk.spring_portfolio.mvc.stocks;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findById(Integer playerid);
    User findByUsername(String username);
    Optional<User> findById(Long playerId);
}