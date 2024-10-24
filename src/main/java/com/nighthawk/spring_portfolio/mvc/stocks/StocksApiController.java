package com.nighthawk.spring_portfolio.mvc.stocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // annotation to simplify the creation of RESTful web services
@RequestMapping("/api/stocks")  // all requests in file begin with this URI
public class StocksApiController {

    // Autowired enables Control to connect URI request and POJO Object to easily for Database CRUD operations
    @Autowired
    private StocksJpaRepository repository;

    /* GET List of Stocks
     * @GetMapping annotation is used for mapping HTTP GET requests onto specific handler methods.
     */
    @GetMapping("/")
    public ResponseEntity<List<Stocks>> getStocks() {
        // ResponseEntity returns List of Stocks provide by JPA findAll()
        return new ResponseEntity<>( repository.findAll(), HttpStatus.OK);
    }

    /* Update Like
     * @PutMapping annotation is used for mapping HTTP PUT requests onto specific handler methods.
     * @PathVariable annotation extracts the templated part {id}, from the URI
     */
    @PostMapping("/like/{id}")
    public ResponseEntity<Stocks> setLike(@PathVariable long id) {
        /* 
        * Optional (below) is a container object which helps determine if a result is present. 
        * If a value is present, isPresent() will return true
        * get() will return the value.
        */
        Optional<Stocks> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Stocks stocks = optional.get();  // value from findByID
            stocks.setHaha(stocks.getHaha()+1); // increment value
            repository.save(stocks);  // save entity
            return new ResponseEntity<>(stocks, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Failed HTTP response: status code, headers, and body
    }

    /* Update Jeer
     */
    @PostMapping("/jeer/{id}")
    public ResponseEntity<Stocks> setJeer(@PathVariable long id) {
        Optional<Stocks> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Stocks stocks = optional.get();
            stocks.setBoohoo(stocks.getBoohoo()+1);
            repository.save(stocks);
            return new ResponseEntity<>(stocks, HttpStatus.OK);
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
