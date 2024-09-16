package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The PersonJpaRepository interface is automatically implemented by Spring Data JPA at runtime.
 * It uses the Java Persistence API (JPA) - specifically, Hibernate - to map, store, update, and retrieve data from relational databases.
 * 
 * This interface extends JpaRepository, which provides CRUD (Create, Read, Update, Delete) operations.
 * Through the JPA the developer can store and retrieve Java objects to and from the database.
 * 
 * JpaRepository is a generic interface and requires two parameters:
 * 1. The entity type to be persisted (in this case, Person).
 * 2. The type of the entity's primary key (in this case, Long).
 * 
 * JpaRepository provides several methods for interacting with the database, including:
 * - save(T entity): save an entity to the database. If T.id exists update, Else create/insert.
 * - findById(ID id): retrieve an entity by its id.
 * - existsById(ID id): check if an entity with the given id exists.
 * - findAll(): retrieve all entities.
 * - deleteById(ID id): delete the entity with the given id from the database.
 * 
 * When a method from this interface is called, the call is intercepted by a Spring Data JPA proxy, which directs the call to the appropriate method in the generated implementation.
 */
public interface PersonJpaRepository extends JpaRepository<Person, Long> {

    /**
     * Query methods defined by Spring Data JPA naming conventions.
     * Spring Data JPA will automatically generate a query using the method name.
     */
    Person findByEmail(String email);
    List<Person> findAllByOrderByNameAsc();
    List<Person> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    Person findByEmailAndPassword(String email, String password);

    /**
     * Custom JPA query using the @Query annotation.
     * This allows for more complex queries that can't be expressed through the method name.
     * The query will find all Person entities where the name or email contains the given term.
     * The 'nativeQuery = true' parameter indicates that the query is a native SQL query, not a JPQL query.
     */
    @Query(
            value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1",
            nativeQuery = true)
    List<Person> findByLikeTermNative(String term);
}