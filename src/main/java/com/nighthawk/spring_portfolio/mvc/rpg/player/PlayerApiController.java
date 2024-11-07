package com.nighthawk.spring_portfolio.mvc.rpg.player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;

@RestController
@RequestMapping("/rpg_player")
public class PlayerApiController {

    @Autowired

    private PlayerJpaRepository playerJpaRepository;
    @Autowired
    private PlayerDetailsService playerDetailsService;
    
    @GetMapping("/player")
    public ResponseEntity<Player> getPlayer(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();  // Email is mapped/unmapped to username for Spring Security

        // Find a Player by username
        Player player = playerJpaRepository.findByEmail(email);

        // Return the Player if found
        if (player != null) {
            return new ResponseEntity<>(player, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getPlayers() {
        return new ResponseEntity<>( playerJpaRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable long id) {
        Optional<Player> optional = playerJpaRepository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Player player = optional.get();  // value from findByID
            return new ResponseEntity<>(player, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);       
    }
    @DeleteMapping("/player/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable long id) {
        Optional<Player> optional = playerJpaRepository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Player player = optional.get();  // value from findByID
            playerJpaRepository.deleteById(id);  // value from findByID
            return new ResponseEntity<>(player, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
    }
    
    @Getter 
    public static class PlayerDto {
        private String email;
        private String password;
        private String name;
        private List<PlayerCsClass> csClasses;
    }

    @PostMapping("/player")
    public ResponseEntity<Object> postPlayer(@RequestBody PlayerDto playerDto) {
        // Validate dob input
        // A Player object WITHOUT ID will create a new record in the database
        Player player = new Player(playerDto.getEmail(), playerDto.getPassword(), playerDto.getName(), playerDetailsService.findCsclass("CSSE"));
        playerDetailsService.savePlayer(player);
        return new ResponseEntity<>(playerDto.getEmail() + " is created successfully", HttpStatus.CREATED);
    }

    /**
     * Search for a Player entity by name or email.
     * @param map of a key-value (k,v), the key is "term" and the value is the search term. 
     * @return A ResponseEntity containing a list of Player entities that match the search term.
     */
    @PostMapping(value = "/players/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> playerSearch(@RequestBody final Map<String,String> map) {
        // extract term from RequestEntity
        String term = (String) map.get("term");

        // JPA query to filter on term
        List<Player> list = playerJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);

        // return resulting list and status, error checking should be added
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    /* 
    @GetMapping("/badgecount")
    public ResponseEntity<Object> getBadgeCount() {

    }
    */


}
