package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "USER";
    private boolean enabled = true;
    private double balance;
    private String stonks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserStock> userStocks;
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

    // Additional methods for stock management...
    public void addStock(String username, int quantity, String stockSymbol) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                        throw new RuntimeException("Not enough stock quantity to remove");
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

    @PostMapping("/login") // New endpoint for user login
    @ResponseBody
    public String loginUser(@RequestBody UserLoginRequest request) {
        Optional<User> userOptional = userService.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if the password matches the stored encoded password
            if (userService.checkPassword(request.getPassword(), user.getPassword())) {
                return "User logged in successfully!";
            }
        }
        return "Invalid username or password!";
    }

    @PostMapping("/addStock")
    @ResponseBody
    public String addStock(@RequestBody StockRequest request) {
        try {
            userService.addStock(request.getUsername(), request.getQuantity(), request.getStockSymbol());
            return "Stock added successfully!";
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
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
}