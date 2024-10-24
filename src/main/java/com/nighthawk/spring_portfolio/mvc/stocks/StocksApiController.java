package com.nighthawk.spring_portfolio.mvc.stocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
public class StocksApiController {

    @Autowired
    private StocksJpaRepository stockRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private UserStockJpaRepository userStockRepository;

    // Get all available stocks
    @GetMapping("/all")
    public ResponseEntity<List<Stocks>> getAllStocks() {
        return new ResponseEntity<>(stockRepository.findAll(), HttpStatus.OK);
    }

    // Buy stock
    @PostMapping("/buy/{userId}/{stockId}/{quantity}")
    public ResponseEntity<?> buyStock(@PathVariable Long userId, @PathVariable Long stockId, @PathVariable int quantity) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Stocks> stockOpt = stockRepository.findById(stockId);
        
        if (userOpt.isPresent() && stockOpt.isPresent()) {
            User user = userOpt.get();
            Stocks stock = stockOpt.get();
            
            double totalCost = stock.getCurrentPrice() * quantity;
            if (user.getBalance() >= totalCost) {
                user.setBalance(user.getBalance() - totalCost); // Deduct cost from user's balance

                // Check if the user already owns this stock
                UserStock userStock = userStockRepository.findByUserAndStock(user, stock)
                        .orElse(new UserStock(null, user, stock, 0, stock.getCurrentPrice()));

                userStock.setQuantity(userStock.getQuantity() + quantity); // Add to owned quantity
                userStockRepository.save(userStock);
                userRepository.save(user);

                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not enough balance to complete purchase", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Sell stock
    @PostMapping("/sell/{userId}/{stockId}/{quantity}")
    public ResponseEntity<?> sellStock(@PathVariable Long userId, @PathVariable Long stockId, @PathVariable int quantity) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Stocks> stockOpt = stockRepository.findById(stockId);

        if (userOpt.isPresent() && stockOpt.isPresent()) {
            User user = userOpt.get();
            Stocks stock = stockOpt.get();

            Optional<UserStock> userStockOpt = userStockRepository.findByUserAndStock(user, stock);
            if (userStockOpt.isPresent() && userStockOpt.get().getQuantity() >= quantity) {
                UserStock userStock = userStockOpt.get();
                userStock.setQuantity(userStock.getQuantity() - quantity);

                if (userStock.getQuantity() == 0) {
                    userStockRepository.delete(userStock); // If no stock left, remove the entry
                } else {
                    userStockRepository.save(userStock); // Save updated stock quantity
                }

                double totalValue = stock.getCurrentPrice() * quantity;
                user.setBalance(user.getBalance() + totalValue); // Add sale value to user's balance
                userRepository.save(user);

                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not enough stock to sell", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Simulate price fluctuation for all stocks
    @PostMapping("/simulate")
    public ResponseEntity<?> simulatePriceFluctuation() {
        List<Stocks> stocks = stockRepository.findAll();
        for (Stocks stock : stocks) {
            stock.fluctuatePrice(); // Simulate price change
            stockRepository.save(stock); // Save updated price
        }
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }
}