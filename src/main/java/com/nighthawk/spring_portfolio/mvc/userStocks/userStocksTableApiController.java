package com.nighthawk.spring_portfolio.mvc.userStocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Controller
@RequestMapping("/stocks/table")
public class userStocksTableApiController {
    @Autowired
    private UserStocksTableService userService;
   
    @PostMapping("/addStock")
    @ResponseBody
    public ResponseEntity<String> addStock(@RequestBody StockRequest request) {
        try {
            userService.addStock(request.getUsername(), request.getQuantity(), request.getStockSymbol());
            return ResponseEntity.ok("Stock added successfully!");
        } catch (ResponseStatusException e) {
            // Use getStatusCode() to retrieve the HTTP status code
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An unexpected error occurred: " + e.getMessage());
        }
    }


}
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserStockInfo {
    private String stockSymbol;
    private int quantity;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
class StockRequest {
    private String username;
    private int quantity;
    private String stockSymbol;
    private String balance;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserLoginRequest {
    private String username;
    private String password;
}

@Service
class UserStocksTableService implements UserDetailsService {
    @Autowired
    private UserStocksRepository userRepository;
    
    //@Autowired
    //private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userStocksTable user = userRepository.findByPersonName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPerson_name())
                //.password(user.getPassword())
                //.authorities(authorities)
                //.accountLocked(!user.isEnabled())
                .build();
    }

    public Optional<userStocksTable> findByUsername(String username) {
        return userRepository.findByPersonName(username);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Method to fetch the current stock price
    public double getCurrentStockPrice(String stockSymbol) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + stockSymbol;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                return jsonResponse.getJSONObject("chart").getJSONArray("result").getJSONObject(0)
                        .getJSONObject("meta").getDouble("regularMarketPrice");
            }
        } catch (Exception e) {
            System.out.println("Error fetching stock price: " + e.getMessage());
        }
        throw new RuntimeException("Failed to fetch stock price for " + stockSymbol);
    }
        // New method to calculate total portfolio value

        public double calculatePortfolioValue(String username) {

            userStocksTable user = userRepository.findByPersonName(username)
    
                                      .orElseThrow(() -> new RuntimeException("User not found"));
    
    
    
            double totalValue = Double.parseDouble(user.getBalance());
    
    
    
            if (user.getStonks() != null && !user.getStonks().isEmpty()) {
    
                String[] stocks = user.getStonks().split(",");
    
    
    
                for (String stock : stocks) {
    
                    String[] parts = stock.split("-");
    
                    int quantity = Integer.parseInt(parts[0]);
    
                    String stockSymbol = parts[1];
    
                    
    
                    double stockPrice = getCurrentStockPrice(stockSymbol);
    
                    totalValue += stockPrice * quantity;
    
                }
    
            }
    
    
    
            return totalValue;
    
        }

    public void addStock(String username, int quantity, String stockSymbol) {
        
        userStocksTable user = userRepository.findByPersonName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double totalValue = Double.parseDouble(user.getBalance());

        double stockPrice = getCurrentStockPrice(stockSymbol);
        double totalCost = stockPrice * quantity;

        if (totalValue < totalCost) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough balance to purchase stock.");
        }

        String str = String.valueOf(totalValue - totalCost);

        user.setBalance(str); // Deduct the balance for purchase

        String existingStonks = user.getStonks();
        StringBuilder updatedStonks = new StringBuilder();
        boolean stockExists = false;

        if (existingStonks != null && !existingStonks.isEmpty()) {
            String[] stocks = existingStonks.split(",");
            
            for (String stock : stocks) {
                String[] parts = stock.split("-");
                int currentQuantity = Integer.parseInt(parts[0]);
                String currentStockSymbol = parts[1];

                if (currentStockSymbol.equals(stockSymbol)) {
                    currentQuantity += quantity;
                    stockExists = true;
                }
                
                updatedStonks.append(currentQuantity).append("-").append(currentStockSymbol).append(",");
            }
        }

        if (!stockExists) {
            updatedStonks.append(quantity).append("-").append(stockSymbol).append(",");
        }

        if (updatedStonks.length() > 0) {
            updatedStonks.setLength(updatedStonks.length() - 1);
        }
        
        user.setStonks(updatedStonks.toString());
        userRepository.save(user);
    }

    public List<UserStockInfo> getUserStocks(String username) {
        userStocksTable user = userRepository.findByPersonName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserStockInfo> stockList = new ArrayList<>();
        
        if (user.getStonks() != null && !user.getStonks().isEmpty()) {
            String[] stocks = user.getStonks().split(",");
            
            for (String stock : stocks) {
                String[] parts = stock.split("-");
                int quantity = Integer.parseInt(parts[0]);
                String stockSymbol = parts[1];
                stockList.add(new UserStockInfo(stockSymbol, quantity));
            }
        }

        return stockList;
    }

    // public User updateBalance(long questionId, long answerId, long userId) {

    //     // Fetch the question, answer, and user from the database
    //     Optional<Question> questionOpt = questionJpaRepository.findById(questionId);
        
    //     Optional<User> userOpt = userRepository.findById(userId);
    
    //     // Check if the entities are present
    //     if (questionOpt.isPresent() && userOpt.isPresent()) {
    //         Question question = questionOpt.get();
            
    //         User user = userOpt.get();
    
    //         double questionPoints = question.getPoints();
            
    //         user.setBalance(user.getBalance() + questionPoints);
    
    //         userRepository.save(user);
    
    //         return user;
    //     } else {
    //         // Handle cases where any of the entities are not found
    //         System.out.println("Question, Answer, or User not found.");
    //         return null;
    //     }
    // }
}