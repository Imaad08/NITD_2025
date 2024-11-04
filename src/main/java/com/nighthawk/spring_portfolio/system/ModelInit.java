package com.nighthawk.spring_portfolio.system;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nighthawk.spring_portfolio.mvc.announcement.Announcement;
import com.nighthawk.spring_portfolio.mvc.announcement.AnnouncementJPA;
import com.nighthawk.spring_portfolio.mvc.jokes.Jokes;
import com.nighthawk.spring_portfolio.mvc.jokes.JokesJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.leaderboard.Leaderboard;
import com.nighthawk.spring_portfolio.mvc.rpg.leaderboard.LeaderboardJpaRepository;
import com.nighthawk.spring_portfolio.mvc.note.Note;
import com.nighthawk.spring_portfolio.mvc.note.NoteJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.person.PersonRole;
import com.nighthawk.spring_portfolio.mvc.person.PersonRoleJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.player.Player;
import com.nighthawk.spring_portfolio.mvc.rpg.player.PlayerCsClass;
import com.nighthawk.spring_portfolio.mvc.rpg.player.PlayerCsClassJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.player.PlayerDetailsService;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;
import com.nighthawk.spring_portfolio.mvc.rpg.question.QuestionJpaRepository;


@Component
@Configuration // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {  
    @Autowired JokesJpaRepository jokesRepo;
    @Autowired LeaderboardJpaRepository leaderboardJpaRepository;
    @Autowired NoteJpaRepository noteRepo;
    @Autowired PersonRoleJpaRepository roleJpaRepository;
    @Autowired PersonDetailsService personDetailsService;

    @Autowired PlayerCsClassJpaRepository csclassJpaRepository;
    @Autowired PlayerDetailsService playerDetailsService;


    @Autowired AnnouncementJPA announcementJPA;

    @Autowired QuestionJpaRepository questionJpaRepository;

    @Bean
    @Transactional
    CommandLineRunner run() {  // The run() method will be executed after the application starts
        return args -> {
            
            // Announcement API is populated with starting announcements
            List<Announcement> announcements = Announcement.init();
            for (Announcement announcement : announcements) {
                Announcement announcementFound = announcementJPA.findByAuthor(announcement.getAuthor());  // JPA lookup
                if (announcementFound == null) {
                    announcementJPA.save(new Announcement(announcement.getAuthor(), announcement.getTitle(), announcement.getBody(), announcement.getTags())); // JPA save
                }
            }


            // Joke database is populated with starting jokes
            String[] jokesArray = Jokes.init();
            for (String joke : jokesArray) {
                List<Jokes> jokeFound = jokesRepo.findByJokeIgnoreCase(joke);  // JPA lookup
                if (jokeFound.size() == 0)
                    jokesRepo.save(new Jokes(null, joke, 0, 0)); //JPA save
            }

            Leaderboard[] leaders = Leaderboard.init();
            for (Leaderboard leader: leaders) {
                Leaderboard leaderboardFound = leaderboardJpaRepository.findByPlayerName(String playerName);
                if (leaderboardFound == null) {
                    leaderboardJpaRepository.save(new Leaderboard(leaderboard.get))
                }
            }

            Question[] questionArray = Question.init();
            for (Question question : questionArray) {
                Question questionFound = questionJpaRepository.findByTitle(question.getTitle());
                if (questionFound == null) {
                    questionJpaRepository.save(new Question(question.getTitle(), question.getContent(), question.getBadge_name(), question.getPoints()));
                }
            }
 
            // Person database is populated with starting people
            Person[] personArray = Person.init();
            for (Person person : personArray) {
                // Name and email are used to lookup the person
                List<Person> personFound = personDetailsService.list(person.getName(), person.getEmail());  // lookup
                if (personFound.size() == 0) { // add if not found
                    // Roles are added to the database if they do not exist
                    List<PersonRole> updatedRoles = new ArrayList<>();
                    for (PersonRole role : person.getRoles()) {
                        // Name is used to lookup the role
                        PersonRole roleFound = roleJpaRepository.findByName(role.getName());  // JPA lookup
                        if (roleFound == null) { // add if not found
                            // Save the new role to database
                            roleJpaRepository.save(role);  // JPA save
                            roleFound = role;
                        }
                        // Accumulate reference to role from database
                        updatedRoles.add(roleFound);
                    }
                    // Update person with roles from role databasea
                    person.setRoles(updatedRoles); // Object reference is updated

                    // Save person to database
                    personDetailsService.save(person); // JPA save

                    // Add a "test note" for each new person
                    String text = "Test " + person.getEmail();
                    Note n = new Note(text, person);  // constructor uses new person as Many-to-One association
                    noteRepo.save(n);  // JPA Save                  
                }
            }


            Player[] playerArray = Player.init();
            for (Player player : playerArray) {
                // Name and email are used to lookup the player
                List<Player> playerFound = playerDetailsService.searchPlayers(player.getName(), player.getEmail());  // lookup
                if (playerFound.size() == 0) { // add if not found
                    // Roles are added to the database if they do not exist
                    List<PlayerCsClass> updatedCsClasses = new ArrayList<>();
                    for (PlayerCsClass csclass : player.getCsclasses()) {
                        // Name is used to lookup the role
                        PlayerCsClass csclassFound = csclassJpaRepository.findByName(csclass.getName());  // JPA lookup
                        if (csclassFound == null) { // add if not found
                            // Save the new role to database
                            csclassJpaRepository.save(csclass);  // JPA save
                            csclassFound = csclass;
                        }
                        // Accumulate reference to role from database
                        updatedCsClasses.add(csclassFound);
                    }
                    // Update person with roles from role databasea
                    player.setCsclasses(updatedCsClasses); // Object reference is updated

                    // Save player to database
                    playerDetailsService.savePlayer(player); // JPA save
                }
            }




        };
    }
}

