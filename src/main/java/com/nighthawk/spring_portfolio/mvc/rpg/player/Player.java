package com.nighthawk.spring_portfolio.mvc.rpg.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Convert;
import static jakarta.persistence.FetchType.EAGER;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Person is a POJO, Plain Old Java Object.
 * --- @Data is Lombox annotation for @Getter @Setter @ToString @EqualsAndHashCode @RequiredArgsConstructor
 * --- @AllArgsConstructor is Lombox annotation for a constructor with all arguments
 * --- @NoArgsConstructor is Lombox annotation for a constructor with no arguments
 * --- @Entity annotation is used to mark the class as a persistent Java class.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="player", converter = JsonType.class)
public class Player {

    /** automatic unique identifier for Person record
     * --- Id annotation is used to specify the identifier property of the entity.
     * ----GeneratedValue annotation is used to specify the primary key generation strategy to use.
     * ----- The strategy is to have the persistence provider pick an appropriate strategy for the particular database.
     * ----- GenerationType.AUTO is the default generation type and it will pick the strategy based on the used database.
     */ 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Many to Many relationship with PersonRole
     * --- @ManyToMany annotation is used to specify a many-to-many relationship between the entities.
     * --- FetchType.EAGER is used to specify that data must be eagerly fetched, meaning that it must be loaded immediately.
     * --- Collection is a root interface in the Java Collection Framework, in this case it is used to store PersonRole objects.
     * --- ArrayList is a resizable array implementation of the List interface, allowing all elements to be accessed using an integer index.
     * --- PersonRole is a POJO, Plain Old Java Object. 
     */
    @ManyToMany(fetch = EAGER)
    private Collection<PlayerCsClass> csclasses = new ArrayList<>();

    
    /** email, password, roles are key attributes to login and authentication
     * --- @NotEmpty annotation is used to validate that the annotated field is not null or empty, meaning it has to have a value.
     * --- @Size annotation is used to validate that the annotated field is between the specified boundaries, in this case greater than 5.
     * --- @Email annotation is used to validate that the annotated field is a valid email address.
     * --- @Column annotation is used to specify the mapped column for a persistent property or field, in this case unique and email.
     */
    @NotEmpty
    @Size(min=5)
    @Column(unique=true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    /** name, dob are attributes to describe the person
     * --- @NonNull annotation is used to generate a constructor with AllArgsConstructor Lombox annotation.
     * --- @Size annotation is used to validate that the annotated field is between the specified boundaries, in this case between 2 and 30 characters.
     * --- @DateTimeFormat annotation is used to declare a field as a date, in this case the pattern is specified as yyyy-MM-dd.
     */ 
    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    /** Custom constructor for Person when building a new Person object from an API call
     */
    public Player(String email, String password, String name, PlayerCsClass csclass) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.csclasses.add(csclass);
    }

    /** 1st telescoping method to create a Person object with USER role
     * @param name
     * @param email
     * @param password
     * @return Player
     *  */ 
    public static Player createPlayer(String name, String email, String password) {
        // By default, Spring Security expects roles to have a "ROLE_" prefix.
        return createPlayer(name, email, password, Arrays.asList("CLASS_CSSE"));
    }
    /** 2nd telescoping method to create a Person object with parameterized roles
     * @param csclasses 
     */
    public static Player createPlayer(String name, String email, String password, List<String> csclassNames) {
        Player player = new Player();
        player.setName(name);
        player.setEmail(email);
        player.setPassword(password);
    
        List<PlayerCsClass> csclasses = new ArrayList<>();
        for (String className : csclassNames) {
            csclasses.add(new PlayerCsClass(className));  // Ensure constructor exists
        }
        player.setCsclasses(csclasses);
    
        return player;
    }
    
   
    /** Static method to initialize an array list of Person objects 
     * @return Person[], an array of Person objects
     */
    public static Player[] init() {
        ArrayList<Player> players = new ArrayList<>();
        
        players.add(createPlayer("Saathvik Gampa", "sg@gmail.com", "123sg"));
        return players.toArray(new Player[0]);
    }
    
}