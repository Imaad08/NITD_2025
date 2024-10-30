package com.nighthawk.spring_portfolio.mvc.stocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Unique username

    @Column(nullable = false)
    private String password; // Encrypted password

    private String role = "USER"; // Role, e.g., "USER" or "ADMIN"
    private boolean enabled = true; // Account status
    private double balance; // User's funds

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
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
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
}