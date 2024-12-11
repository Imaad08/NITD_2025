package com.nighthawk.spring_portfolio.mvc.userStocks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Controller class for handling the user stock-related API endpoints.
 */
@Controller
@RequestMapping("/stocks/table")
public class userStocksTableApiController {

    // Injecting the service that handles the core logic for stock management
    @Autowired
    private UserStocksTableService userService;

    /**
     * API endpoint to add a stock to a user's portfolio.
     * 
     * @param request Contains stock details (username, quantity, stock symbol).
     * @return ResponseEntity with a success or error message.
     */
    @PostMapping("/addStock")
    @ResponseBody
    public ResponseEntity<String> addStock(@RequestBody StockRequest request) {
        try {
            // Calling the service method to add the stock
            userService.addStock(request.getUsername(), request.getQuantity(), request.getStockSymbol());
            return ResponseEntity.ok("Stock added successfully!");
        } catch (ResponseStatusException e) {
            // Handling any errors during stock addition
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            // Catching unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * API endpoint to remove a stock from a user's portfolio.
     * 
     * @param request Contains stock details (username, quantity, stock symbol).
     * @return Success or error message.
     */
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

    /**
     * API endpoint to get all stocks for a user.
     * 
     * @param username The username for which to fetch stocks.
     * @return A list of UserStockInfo containing stock symbol and quantity.
     */
    @GetMapping("/getStocks")
    @ResponseBody
    public List<UserStockInfo> getStocks(@RequestParam String username) {
        return userService.getUserStocks(username);
    }

    /**
     * API endpoint to calculate the total value of a user's stock portfolio.
     * 
     * @param username The username whose portfolio value is being calculated.
     * @return Portfolio value as a double.
     */
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

/**
 * Data Transfer Object (DTO) to represent a user's stock information (stock symbol and quantity).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserStockInfo {
    private String stockSymbol;
    private int quantity;
}

/**
 * DTO to represent the details for adding/removing stocks (username, quantity, stock symbol, balance).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class StockRequest {
    private String username;
    private int quantity;
    private String stockSymbol;
    private String balance;
}

/**
 * DTO for user login request (username and password).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserLoginRequest {
    private String username;
    private String password;
}

/**
 * Service class responsible for managing the logic related to user stocks.
 * Implements UserDetailsService for handling user authentication.
 */
@Service
class UserStocksTableService implements UserDetailsService {
    
    // Injecting necessary repositories
    @Autowired
    private UserStocksRepository userRepository;

    @Autowired
    private PersonJpaRepository personJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Loads user by username for authentication.
     * 
     * @param username The username to load user details for.
     * @return User details object.
     * @throws UsernameNotFoundException If user is not found.
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userStocksTable user = userRepository.findByPersonName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPerson_name())
                .build();
    }

    /**
     * Fetches the current stock price for a given stock symbol from Yahoo Finance.
     * 
     * @param stockSymbol The stock symbol for which the price is fetched.
     * @return The current stock price.
     */
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

    /**
     * Calculates the total value of the user's stock portfolio by considering stock prices and quantities.
     * 
     * @param username The username for which to calculate the portfolio value.
     * @return Total value of the portfolio.
     */
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

    /**
     * Adds stock to a user's portfolio after ensuring they have enough balance.
     * 
     * @param username The username of the user.
     * @param quantity The quantity of stocks to add.
     * @param stockSymbol The symbol of the stock to add.
     */
    public void addStock(String username, int quantity, String stockSymbol) {
        userStocksTable user = userRepository.findByPersonName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double totalValue = Double.parseDouble(user.getBalance());
        double stockPrice = getCurrentStockPrice(stockSymbol);
        double totalCost = stockPrice * quantity;

        // Check if the user has enough balance
        if (totalValue < totalCost) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough balance to purchase stock.");
        }

        user.setBalance(String.valueOf(totalValue - totalCost)); // Deduct balance for stock purchase

        // Update the user's stock holdings
        StringBuilder updatedStonks = new StringBuilder();
        boolean stockExists = false;
        String existingStonks = user.getStonks();
        
        if (existingStonks != null && !existingStonks.isEmpty()) {
            String[] stocks = existingStonks.split(",");
            for (String stock : stocks) {
                String[] parts = stock.split("-");
                int currentQuantity = Integer.parseInt(parts[0]);
                String currentStockSymbol = parts[1];

                // Update quantity if stock already exists
                if (currentStockSymbol.equals(stockSymbol)) {
                    currentQuantity += quantity;
                    stockExists = true;
                }
                updatedStonks.append(currentQuantity).append("-").append(currentStockSymbol).append(",");
            }
        }

        // If stock doesn't exist, add it
        if (!stockExists) {
            updatedStonks.append(quantity).append("-").append(stockSymbol).append(",");
        }

        if (updatedStonks.length() > 0) {
            updatedStonks.setLength(updatedStonks.length() - 1); // Remove trailing comma
        }

        user.setStonks(updatedStonks.toString());
        userRepository.save(user);

        // Update balance in the person table
        com.nighthawk.spring_portfolio.mvc.person.Person person = user.getPerson();
        person.setBalance(user.getBalance());
        personJpaRepository.save(person);
    }

    /**
     * Removes stock from the user's portfolio and updates the balance.
     * 
     * @param username The username of the user.
     * @param quantity The quantity of stock to remove.
     * @param stockSymbol The symbol of the stock to remove.
     */
    public void removeStock(String username, int quantity, String stockSymbol) {
        userStocksTable user = userRepository.findByPersonName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double stockPrice = getCurrentStockPrice(stockSymbol);
        double totalValue = stockPrice * quantity;

        StringBuilder updatedStonks = new StringBuilder();
        boolean stockExists = false;
        String existingStonks = user.getStonks();
        
        if (existingStonks != null && !existingStonks.isEmpty()) {
            String[] stocks = existingStonks.split(",");
            for (String stock : stocks) {
                String[] parts = stock.split("-");
                int currentQuantity = Integer.parseInt(parts[0]);
                String currentStockSymbol = parts[1];

                // If stock is found, reduce the quantity
                if (currentStockSymbol.equals(stockSymbol)) {
                    stockExists = true;

                    if (currentQuantity < quantity) {
                        throw new RuntimeException("Not enough stock quantity to remove");
                    }

                    currentQuantity -= quantity;

                    if (currentQuantity > 0) {
                        updatedStonks.append(currentQuantity).append("-").append(currentStockSymbol).append(",");
                    }
                } else {
                    updatedStonks.append(parts[0]).append("-").append(parts[1]).append(",");
                }
            }
        }

        if (!stockExists) {
            throw new RuntimeException("Stock not found in user's portfolio");
        }

        if (updatedStonks.length() > 0) {
            updatedStonks.setLength(updatedStonks.length() - 1); // Remove trailing comma
        }

        user.setStonks(updatedStonks.toString());
        double userBalance = Double.parseDouble(user.getBalance());
        user.setBalance(String.valueOf(userBalance + totalValue)); // Add back the stock value to balance
        userRepository.save(user);

        com.nighthawk.spring_portfolio.mvc.person.Person person = user.getPerson();
        person.setBalance(user.getBalance());
        personJpaRepository.save(person);
    }

    /**
     * Retrieves the user's stocks (symbol and quantity).
     * 
     * @param username The username of the user.
     * @return A list of UserStockInfo.
     */
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
}
