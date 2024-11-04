package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stocks")
public class StocksApiController {

    @Autowired
    private StocksJpaRepository stockRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private UserStockJpaRepository userStockRepository;

    @Value("${yahoofinance.quotesquery1v8.enabled:false}")
    private boolean isV8Enabled;

    // Get stock by ticker symbol
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol) {
        try {
            System.out.println("Fetching stock data for symbol: " + symbol); 
            
            String url = isV8Enabled
                    ? "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol
                    : "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Successfully fetched stock data for: " + symbol);
                return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
            } else {
                System.out.println("No stock data returned for symbol: " + symbol);
                return new ResponseEntity<>("Stock not found for symbol: " + symbol, HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            System.out.println("Error occurred while fetching stock data: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Failed to retrieve stock data for " + symbol, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
