package com.nighthawk.spring_portfolio.mvc.note;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nighthawk.spring_portfolio.mvc.person.Person;

public interface NoteJpaRepository extends JpaRepository<Note, Long> {
    List<Person> findByPersonId(Long id);

    @Transactional
    void deleteByPersonId(long id);
}

