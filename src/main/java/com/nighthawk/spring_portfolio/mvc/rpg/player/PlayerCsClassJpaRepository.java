package com.nighthawk.spring_portfolio.mvc.rpg.player;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  PlayerCsClassJpaRepository extends JpaRepository<PlayerCsClass, Long> {
    PlayerCsClass findByName(String name);
}
