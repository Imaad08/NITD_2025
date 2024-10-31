package com.nighthawk.spring_portfolio.mvc.poker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/casino/poker")
public class PokerApiController {

    private PokerBoard pokerBoard;

    // Play endpoint
    @PostMapping("/play")
    public ResponseEntity<GameResult> playGame(@RequestBody BetRequest betRequest) {
        try {
            if (pokerBoard == null) {
                System.out.println("Initializing new PokerBoard instance for this game.");
                pokerBoard = new PokerBoard();  // Initialize if not done
            }

            System.out.println("Received bet: " + betRequest.getBet());
            pokerBoard.dealHands();  // Deal hands for the game
            System.out.println("Dealt player hand: " + pokerBoard.getPlayerHand());
            System.out.println("Dealt dealer hand: " + pokerBoard.getDealerHand());

            GameResult result = new GameResult(pokerBoard.getPlayerHand(), pokerBoard.getDealerHand(), betRequest.getBet());
            System.out.println("GameResult created: " + result);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred during playGame processing.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Reset endpoint
    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        pokerBoard = new PokerBoard();
        System.out.println("Poker game has been reset. New PokerBoard instance created.");
        return new ResponseEntity<>("Game has been reset.", HttpStatus.OK);
    }
}
