package com.nighthawk.spring_portfolio.mvc.poker;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;

@RestController
@RequestMapping("/api/casino/poker")
public class PokerApiController {

    @Autowired
    private UserJpaRepository userJpaRepository;

    private PokerBoard pokerBoard;

    // Request class to receive bet and username from client
    public static class PokerRequest {
        private double bet;
        private String username;

        public double getBet() { return bet; }
        public void setBet(double bet) { this.bet = bet; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    // Response class to send game results back to the client
    public static class PokerResponse {
        private List<Card> playerHand;
        private List<Card> dealerHand;
        private double updatedBalance;
        private boolean playerWin;
        private double bet;

        public PokerResponse(List<Card> playerHand, List<Card> dealerHand, double updatedBalance, boolean playerWin, double bet) {
            this.playerHand = playerHand;
            this.dealerHand = dealerHand;
            this.updatedBalance = updatedBalance;
            this.playerWin = playerWin;
            this.bet = bet;
        }

        public List<Card> getPlayerHand() { return playerHand; }
        public List<Card> getDealerHand() { return dealerHand; }
        public double getUpdatedBalance() { return updatedBalance; }
        public boolean isPlayerWin() { return playerWin; }
        public double getBet() { return bet; }
    }

    @PostMapping("/play")
    public ResponseEntity<PokerResponse> playGame(@RequestBody PokerRequest pokerRequest) {
        if (pokerBoard == null) {
            pokerBoard = new PokerBoard();  // Initialize if not done
        }

        Optional<User> optionalUser = userJpaRepository.findByUsername(pokerRequest.getUsername());
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // User not found
        }

        User user = optionalUser.get();
        double currentBalance = user.getBalance();

        // Deal hands for the game
        pokerBoard.dealHands();

        // Calculate win/loss and update user balance
        GameResult result = new GameResult(pokerBoard.getPlayerHand(), pokerBoard.getDealerHand(), pokerRequest.getBet());
        boolean playerWin = result.isPlayerWin();
        double winnings = playerWin ? pokerRequest.getBet() : -pokerRequest.getBet();
        double updatedBalance = currentBalance + winnings;

        // Update the user's balance in the database
        user.setBalance(updatedBalance);
        userJpaRepository.save(user);

        // Create and return the response object with game results and updated balance
        PokerResponse response = new PokerResponse(
            pokerBoard.getPlayerHand(),
            pokerBoard.getDealerHand(),
            updatedBalance,
            playerWin,
            pokerRequest.getBet()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        pokerBoard = new PokerBoard();
        return new ResponseEntity<>("Game has been reset.", HttpStatus.OK);
    }
}
