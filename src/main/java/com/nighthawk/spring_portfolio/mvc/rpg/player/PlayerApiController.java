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


/**
 * This class provides RESTful API endpoints for managing Player entities.
 * It includes endpoints for creating, retrieving, updating, and deleting Player entities.
 */
@RestController
@RequestMapping("/rpg_player")
public class PlayerApiController {
    /*
    #### RESTful API REFERENCE ####
    Resource: https://spring.io/guides/gs/rest-service/
    */

    /**
     * Repository for accessing Player entities in the database.
     */
    @Autowired
    private PlayerJpaRepository playerJpaRepository;
    /**
     * Service for managing Player entities.
     */
    @Autowired
    private PlayerDetailsService playerDetailsService;
    

    /**
     * Retrieves a Player entity by current user of JWT token.
     * @return A ResponseEntity containing the Player entity if found, or a NOT_FOUND status if not found.
     */
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
    
    /**
     * Retrieves all the Player entities in the database, people
     * @return A ResponseEntity containing a list for Player entities 
     */
    @GetMapping("/players")
    public ResponseEntity<List<Player>> getPlayers() {
        return new ResponseEntity<>( playerJpaRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /**
     * Retrieves a Player entity by its ID.
     *
     * @param id The ID of the Player entity to retrieve.
     * @return A ResponseEntity containing the Player entity if found, or a NOT_FOUND status if not found.
     */
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

    /**
     * Delete a Player entity by its ID.
     *
     * @param id The ID of the Player entity to delete.
     * @return A ResponseEntity containing the Player entity if deleted, or a NOT_FOUND status if not found.
     */
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

    /* DTO (Data Transfer Object) to support POST request for postPlayer method
       .. represents the data in the request body
     */
    @Getter 
    public static class PlayerDto {
        private String email;
        private String password;
        private String name;
        private List<PlayerCsClass> csClasses;
    }

    /**
     * Create a new Player entity.
     * @param playerDto
     * @return A ResponseEntity containing a success message if the Player entity is created, or a BAD_REQUEST status if not created.
     */
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
