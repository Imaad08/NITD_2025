package com.nighthawk.spring_portfolio.mvc.dice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.stocks.UserStock;
import com.nighthawk.spring_portfolio.mvc.stocks.UserStockJpaRepository;

import lombok.Getter;

@RestController
@RequestMapping("/api/casino/dice")
public class DiceApiController {

    @Autowired
    private UserStockJpaRepository userStockJpaRepository;

    @Getter 
    public static class DiceRequest {
        private double winChance;
        private double betSize;
        private String username;
    }

    @PostMapping("/calculate")
    public ResponseEntity<UserStock> postDice(@RequestBody DiceRequest diceRequest) {
        System.out.println("Received request: " + diceRequest);
        Dice dice = new Dice(diceRequest.getWinChance(), diceRequest.getBetSize());

        Optional<UserStock> optionalUserStock = userStockJpaRepository.findByUsername(diceRequest.getUsername());
        if (optionalUserStock.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserStock userStock = optionalUserStock.get();
        double currentBalance = userStock.getUser().getBalance();
        userStock.getUser().setBalance(currentBalance + dice.calculateWin());
        userStockJpaRepository.save(userStock);  // Save the updated balance

        return new ResponseEntity<>(userStock, HttpStatus.OK);  // Return updated user data
    }
}
