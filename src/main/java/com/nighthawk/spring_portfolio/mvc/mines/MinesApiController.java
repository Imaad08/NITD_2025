package com.nighthawk.spring_portfolio.mvc.mines;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/casino/mines")
public class MinesApiController {
    MinesBoard board;

    @GetMapping("/{xCoord}/{yCoord}")
    public ResponseEntity<Boolean> getMine(@PathVariable int xCoord, @PathVariable int yCoord) {
        return new ResponseEntity<>(board.checkMine(xCoord, yCoord), HttpStatus.OK);
    }

    @GetMapping("/winnings")
    public ResponseEntity<Double> getWinnings(@PathVariable double pts) {
        return new ResponseEntity<>(board.winnings(), HttpStatus.OK);
    }

    @PostMapping("/stakes/{stakes}")
    public ResponseEntity<String> postStakes(@PathVariable String stakes) {
        board = new MinesBoard(stakes);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
