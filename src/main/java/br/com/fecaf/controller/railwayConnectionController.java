package br.com.fecaf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class RailwayConnectionController {
    
    @GetMapping("/")
    public String railwayConnection() {
        return "API está no ar!";
    }

}
