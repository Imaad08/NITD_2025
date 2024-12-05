package com.nighthawk.spring_portfolio.mvc.stocksFetch;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stocks")
public class StocksApiController {

    //@Autowired
    //private StocksJpaRepository stockRepository;

    //@Autowired
    //private UserJpaRepository userRepository;

    //@Autowired
    //private UserStockJpaRepository userStockRepository;

    @Value("${yahoofinance.quotesquery1v8.enabled:false}")
    private boolean isV8Enabled;

    // Get stock by ticker symbol
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol) {
        try {
            System.out.println("Fetching stock data for symbol: " + symbol); 
            
            String url = isV8Enabled
                    ? "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol
                    : "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Successfully fetched stock data for: " + symbol);
                return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
            } else {
                System.out.println("No stock data returned for symbol: " + symbol);
                return new ResponseEntity<>("Stock not found for symbol: " + symbol, HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            System.out.println("Error occurred while fetching stock data: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Failed to retrieve stock data for " + symbol, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

