package com.nighthawk.spring_portfolio.mvc.userStocks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nighthawk.spring_portfolio.mvc.user.User;

public @interface userStocksTableJpaRepository {
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {}

    @Repository
    public interface UserStocksRepository extends JpaRepository<userStocksTable, Long> {}
}
