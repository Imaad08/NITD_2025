package com.nighthawk.spring_portfolio.mvc.greet;
/* MVC code that shows defining a simple Model, calling View, and this file serving as Controller
 * Web Content with Spring MVCSpring Example: https://spring.io/guides/gs/serving-web-con
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller  // HTTP requests are handled as a controller, using the @Controller annotation
public class GreetViewController {

    // @GetMapping handles GET request for /greet, maps it to greeting() method
    @GetMapping("/greet")
    // @RequestParam handles variables binding to frontend, defaults, etc
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        // model attributes are visible to Thymeleaf when HTML is "pre-processed"
        model.addAttribute("name", name);

        // load HTML VIEW (greet.html)
        return "greet"; 

    }

}