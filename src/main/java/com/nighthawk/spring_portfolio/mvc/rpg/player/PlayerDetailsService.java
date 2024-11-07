package com.nighthawk.spring_portfolio.mvc.rpg.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class PlayerDetailsService implements UserDetailsService {

    @Autowired
    private PlayerJpaRepository playerJpaRepository;  // Original name retained

    @Autowired
    private PlayerCsClassJpaRepository playerCsClassJpaRepository;  // Original name retained


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Player player = playerJpaRepository.findByEmail(email);
        if (player == null) {
            throw new UsernameNotFoundException("Player not found with email: " + email);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        player.getCsclasses().forEach(csClass -> {
            authorities.add(new SimpleGrantedAuthority(csClass.getName()));
        });

        return new User(player.getEmail(), player.getPassword(), authorities);
    }

    public List<Player> listAllPlayers() {
        return playerJpaRepository.findAllByOrderByNameAsc();
    }

    public List<Player> searchPlayers(String name, String email) {
        return playerJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email);
    }

    public List<Player> searchPlayersByTerm(String term) {
        return playerJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    public List<Player> searchPlayersByTermNative(String term) {
        String likeTerm = "%" + term + "%";
        return playerJpaRepository.findByLikeTermNative(likeTerm);
    }

    public void savePlayer(Player player) {
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        playerJpaRepository.save(player);
    }

    public Player getPlayerById(long id) {
        return playerJpaRepository.findById(id).orElse(null);
    }

    public Player getPlayerByEmail(String email) {
        return playerJpaRepository.findByEmail(email);
    }

    public void deletePlayer(long id) {
        playerJpaRepository.deleteById(id);
    }

    public void applyDefaultValues(String defaultPassword, String defaultClassName) {
        for (Player player : listAllPlayers()) {
            if (player.getPassword() == null || player.getPassword().isBlank()) {
                player.setPassword(passwordEncoder.encode(defaultPassword));
            }
            if (player.getCsclasses().isEmpty()) {
                PlayerCsClass csClass = playerCsClassJpaRepository.findByName(defaultClassName);
                if (csClass != null) {
                    player.getCsclasses().add(csClass);
                }
            }
        }
    }

    public List<PlayerCsClass> listAllCsClasses() {
        return playerCsClassJpaRepository.findAll();
    }

    public PlayerCsClass findCsclass(String className) {
        return playerCsClassJpaRepository.findByName(className);
    }

    public void addCsClassToPlayer(String email, String className) {
        Player player = playerJpaRepository.findByEmail(email);
        if (player != null) {
            PlayerCsClass csClass = playerCsClassJpaRepository.findByName(className);
            if (csClass != null && !player.getCsclasses().contains(csClass)) {
                player.getCsclasses().add(csClass);
            }
        }
    }

}
