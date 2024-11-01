package com.nighthawk.spring_portfolio.mvc.stocks;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Unique username

    @Column(nullable = false)
    private String password; // Encrypted password

    private String role = "USER"; // Role, e.g., "USER" or "ADMIN"
    private boolean enabled = true; // Account status
    private double balance; // User's funds
    private String stonks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserStock> userStocks; // User's stocks
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
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setBalance(balance);
        return userRepository.save(user);
    }

    public void addStock(String username, int quantity, String stockSymbol) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String newStock = quantity + "-" + stockSymbol;
        if (user.getStonks() == null || user.getStonks().isEmpty()) {
            user.setStonks(newStock);
        } else {
            user.setStonks(user.getStonks() + "," + newStock);
        }
        userRepository.save(user);
    }
}

// Class to encapsulate registration data for JSON request
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
}
