package br.com.fecaf.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class railwayConnectionController {

    @GetMapping("/")
    public String railwayConnection() {
        return "API está no ar!";
    }
    
}
