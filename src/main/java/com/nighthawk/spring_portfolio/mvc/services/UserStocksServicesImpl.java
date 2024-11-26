package com.nighthawk.spring_portfolio.mvc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.nighthawk.spring_portfolio.mvc.repos.UserStocksJpaRepository;
import com.nighthawk.spring_portfolio.mvc.stocks.UserStocks;

public class UserStocksServicesImpl implements UserStocksService {
    @Autowired
    private UserStocksJpaRepository userStocksRepo;
    @Override
    public Optional<List<UserStocks>> getUserStocks(String user_id) {
        return userStocksRepo.findByUser(user_id);
    }

    @Override
    public void addStock(String user_id, int quantity, String stockSymbol) {
        UserStocks userStocks = new UserStocks();
        userStocks.setUser(user_id);
        userStocks.setStonks(quantity +"-"+ stockSymbol);
        userStocksRepo.save(userStocks);
    }
    
}
