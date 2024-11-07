package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;
import com.nighthawk.spring_portfolio.mvc.rpg.question.QuestionJpaRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "USER";
    private boolean enabled = true;
    public double balance;
    private String stonks;

}

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

@Service
class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(!user.isEnabled())
                .build();
    }

    public User registerUser(String username, String password, double balance) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setBalance(balance);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
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

            User user = userRepository.findByUsername(username)
    
                                      .orElseThrow(() -> new RuntimeException("User not found"));
    
    
    
            double totalValue = user.getBalance();
    
    
    
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double stockPrice = getCurrentStockPrice(stockSymbol);
        double totalCost = stockPrice * quantity;

        if (user.getBalance() < totalCost) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough balance to purchase stock.");
        }

        user.setBalance(user.getBalance() - totalCost); // Deduct the balance for purchase

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

    public void removeStock(String username, int quantity, String stockSymbol) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double stockPrice = getCurrentStockPrice(stockSymbol);
        double totalValue = stockPrice * quantity;

        String existingStonks = user.getStonks();
        StringBuilder updatedStonks = new StringBuilder();

        if (existingStonks != null && !existingStonks.isEmpty()) {
            String[] stocks = existingStonks.split(",");
            
            for (String stock : stocks) {
                String[] parts = stock.split("-");
                int currentQuantity = Integer.parseInt(parts[0]);
                String currentStockSymbol = parts[1];

                if (currentStockSymbol.equals(stockSymbol)) {
                    if (currentQuantity < quantity) {
                        throw new RuntimeException("not enough stock quantity to remove");
                    }
                    currentQuantity -= quantity;
                }

                if (currentQuantity > 0) {
                    updatedStonks.append(currentQuantity).append("-").append(currentStockSymbol).append(",");
                }
            }
        }

        if (updatedStonks.length() > 0) {
            updatedStonks.setLength(updatedStonks.length() - 1);
        }

        user.setStonks(updatedStonks.toString());
        user.setBalance(user.getBalance() + totalValue); // Add the balance for sale
        userRepository.save(user);
    }

    public List<UserStockInfo> getUserStocks(String username) {
        User user = userRepository.findByUsername(username)
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

    public User updateBalance(long questionId, long answerId, long userId) {

        // Fetch the question, answer, and user from the database
        Optional<Question> questionOpt = questionJpaRepository.findById(questionId);
        
        Optional<User> userOpt = userRepository.findById(userId);
    
        // Check if the entities are present
        if (questionOpt.isPresent() && userOpt.isPresent()) {
            Question question = questionOpt.get();
            
            User user = userOpt.get();
    
            double questionPoints = question.getPoints();
            
            user.setBalance(user.getBalance() + questionPoints);
    
            userRepository.save(user);
    
            return user;
        } else {
            // Handle cases where any of the entities are not found
            System.out.println("Question, Answer, or User not found.");
            return null;
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
class UserRegistrationRequest {
    private String username;
    private String password;
    private double balance;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserLoginRequest {
    private String username;
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class StockRequest {
    private String username;
    private int quantity;
    private String stockSymbol;
}

@Controller
@RequestMapping("/user")
class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(@RequestBody UserRegistrationRequest request) {
        if (userService.registerUser(request.getUsername(), request.getPassword(), request.getBalance()) != null) {
            return "User registered successfully!";
        }
        return "Registration failed!";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be empty!");
        }
    
        Optional<User> userOptional = userService.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Verify password using password encoder
            if (userService.checkPassword(request.getPassword(), user.getPassword())) {
                // Redirect or inform frontend to redirect to /home
                return ResponseEntity.ok("Redirecting to home");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
    }
        
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


    @PostMapping("/removeStock")
    @ResponseBody
    public String removeStock(@RequestBody StockRequest request) {
        try {
            userService.removeStock(request.getUsername(), request.getQuantity(), request.getStockSymbol());
            return "Stock removed successfully!";
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/getStocks")
    @ResponseBody
    public List<UserStockInfo> getStocks(@RequestParam String username) {
        return userService.getUserStocks(username);
    }
    
    @GetMapping("/portfolioValue")
    @ResponseBody

    public ResponseEntity<Double> getPortfolioValue(@RequestParam String username) {

        try {

            double portfolioValue = userService.calculatePortfolioValue(username);

            return ResponseEntity.ok(portfolioValue);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                                 .body(null);

        }

    }
}