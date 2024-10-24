package com.nighthawk.spring_portfolio.mvc.stocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
public class StocksApiController {

    @Autowired
    private StocksJpaRepository repository;

    // Get all stocks
    @GetMapping("/")
    public ResponseEntity<List<Stocks>> getStocks() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    // Buy stock
    @PostMapping("/buy/{id}/{quantity}")
    public ResponseEntity<Stocks> buyStock(@PathVariable long id, @PathVariable int quantity) {
        Optional<Stocks> optional = repository.findById(id);
        if (optional.isPresent()) {
            Stocks stock = optional.get();
            stock.setOwnedQuantity(stock.getOwnedQuantity() + quantity); // Increase owned quantity
            repository.save(stock);
            return new ResponseEntity<>(stock, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Sell stock
    @PostMapping("/sell/{id}/{quantity}")
    public ResponseEntity<Stocks> sellStock(@PathVariable long id, @PathVariable int quantity) {
        Optional<Stocks> optional = repository.findById(id);
        if (optional.isPresent()) {
            Stocks stock = optional.get();
            if (stock.getOwnedQuantity() >= quantity) {
                stock.setOwnedQuantity(stock.getOwnedQuantity() - quantity); // Decrease owned quantity
                repository.save(stock);
                return new ResponseEntity<>(stock, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Not enough stock to sell
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Update stock price
    @PostMapping("/updatePrice/{id}/{price}")
    public ResponseEntity<Stocks> updateStockPrice(@PathVariable long id, @PathVariable double price) {
        Optional<Stocks> optional = repository.findById(id);
        if (optional.isPresent()) {
            Stocks stock = optional.get();
            stock.setCurrentPrice(price); // Update the stock's price
            repository.save(stock);
            return new ResponseEntity<>(stock, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}