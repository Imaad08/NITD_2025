package com.nighthawk.spring_portfolio.mvc.services;

import java.util.List;
import java.util.Optional;

import com.nighthawk.spring_portfolio.mvc.stocks.UserStocks;

public interface UserStocksService {
     public Optional<List<UserStocks>> getUserStocks(String user_id);
     public void addStock(String user_id, int quantity, String stockSymbol);    
}
