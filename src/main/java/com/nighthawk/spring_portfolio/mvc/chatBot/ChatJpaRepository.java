package com.nighthawk.spring_portfolio.mvc.chatBot;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ChatJpaRepository extends JpaRepository<Chat, Long>{
	
	List<Chat> findByPersonId(Long personId);
	@Transactional
	List<Chat> deleteByPersonId(Long personId);

}
