package com.nighthawk.spring_portfolio.mvc.birds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // HTTP requests are handled as a controller, using the @Controller annotation
public class BirdsViewController {

    // CONTROLLER handles GET request for /birds, maps it to birds() method
    @GetMapping("/birds")
    public String birds() {

        // load HTML VIEW (birds.html)
        return "birds";

    }
}

