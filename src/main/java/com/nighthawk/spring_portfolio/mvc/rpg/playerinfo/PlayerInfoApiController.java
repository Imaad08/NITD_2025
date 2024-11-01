package com.nighthawk.spring_portfolio.mvc.rpg.playerinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.Getter;

@RestController
@RequestMapping("/rpg_playerinfo")
public class PlayerInfoApiController {

    @Autowired
    private PlayerInfoJpaRepository playerInfoJpaRepository;

    @Getter
    public static class PlayerInfoDto {
        private String name;
        private int age;
        private int period;
    }

    @PostMapping("/create")
    public ResponseEntity<PlayerInfo> createPlayerInfo(@RequestBody PlayerInfoDto playerInfoDto) {
        PlayerInfo playerInfo = new PlayerInfo(
            playerInfoDto.getName(),
            playerInfoDto.getAge(),
            playerInfoDto.getPeriod()
        );
        
        playerInfoJpaRepository.save(playerInfo);
        return new ResponseEntity<>(playerInfo, HttpStatus.CREATED);
    }
}
