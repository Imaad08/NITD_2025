package com.nighthawk.spring_portfolio.mvc.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PersonUserMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = false, nullable = false)
    private Long userId;
    @Column(unique = false, nullable = false)
    private Long personId;

    public PersonUserMapping(Long userId, Long personId) {
        this.userId = userId;
        this.personId = personId;
    }


    public static PersonUserMapping createPersonUserMapping(Long userId, Long personId) {
        PersonUserMapping personusermapping = new PersonUserMapping();
        personusermapping.setUserId(userId);
        personusermapping.setPersonId(personId);

        return personusermapping;
    }

    public static PersonUserMapping[] init() {
        ArrayList<PersonUserMapping> personusermappings = new ArrayList<>();
        personusermappings.add(createPersonUserMapping(1L, 2L));

        return personusermappings.toArray(new PersonUserMapping[0]);
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        PersonUserMapping personusermappings[] = init();

        // iterate using "enhanced for loop"
        for( PersonUserMapping personusermapping : personusermappings) {
            System.out.println(personusermapping);  // print object
        }
    }
}
