package com.nighthawk.spring_portfolio.mvc.dice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.stocks.User;
import com.nighthawk.spring_portfolio.mvc.stocks.UserJpaRepository;

import lombok.Getter;

@RestController
@RequestMapping("/api/casino/dice")
public class DiceApiController {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Getter 
    public static class DiceRequest {
        private double winChance;
        private double betSize;
        private String username;
    }

    @PostMapping("/calculate")
    public ResponseEntity<User> postDice(@RequestBody DiceRequest diceRequest) {
        System.out.println("Received request: " + diceRequest);
        Dice dice = new Dice(diceRequest.getWinChance(), diceRequest.getBetSize());
        System.out.println(diceRequest.getUsername());
        
        Optional<User> optionalUser = userJpaRepository.findByUsername(diceRequest.getUsername());
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // User not found
        }

        User user = optionalUser.get();  // Safe to get user now
        System.out.println(user);
        double currentBalance = user.getBalance();
        user.setBalance(currentBalance + dice.calculateWin());
        userJpaRepository.save(user);  // Save the updated balance

        return new ResponseEntity<>(user, HttpStatus.OK);  // Return updated user data
    }

}
