package com.nighthawk.spring_portfolio.mvc.poker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poker")
public class PokerApiController {

    @Autowired
    private PokerJpaRepository repository;

    private PokerBoard pokerBoard;

    // Play endpoint
    @PostMapping("/play")
    public ResponseEntity<GameResult> playGame(@RequestBody BetRequest betRequest) {
        if (pokerBoard == null) {
            pokerBoard = new PokerBoard();  // Initialize if not done
        }

        pokerBoard.dealHands();  // Deal hands for the game
        GameResult result = new GameResult(pokerBoard.getPlayerHand(), pokerBoard.getDealerHand(), betRequest.getBet());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Reset endpoint
    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        pokerBoard = new PokerBoard();
        return new ResponseEntity<>("Game has been reset.", HttpStatus.OK);
    }
}
