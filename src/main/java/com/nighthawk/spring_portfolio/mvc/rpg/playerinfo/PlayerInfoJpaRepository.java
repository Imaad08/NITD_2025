package com.nighthawk.spring_portfolio.mvc.rpg.playerinfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerInfoJpaRepository extends JpaRepository<PlayerInfo, Long> {
    // Additional custom queries can be added here if needed
}
