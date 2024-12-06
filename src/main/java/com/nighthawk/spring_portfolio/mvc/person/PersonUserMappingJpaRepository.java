package com.nighthawk.spring_portfolio.mvc.person;



import org.springframework.data.jpa.repository.JpaRepository;



public interface  PersonUserMappingJpaRepository extends JpaRepository<PersonUserMapping, Long> {
    PersonUserMapping findByUserId(Long User_id);
}
