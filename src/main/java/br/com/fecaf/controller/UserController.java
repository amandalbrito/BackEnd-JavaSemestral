package br.com.fecaf.controller;

import br.com.fecaf.model.User;
import br.com.fecaf.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/listarUsers")
    public List<User> listarUsers() {
        return userService.listarUsers();
    }

    @PostMapping("/cadastrarUser")
    public ResponseEntity<User> salvarUser(@RequestBody User user) {
        User newUser = userService.salvarUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/deletarUser/{id}")
    public ResponseEntity<Void> deletarUser(@PathVariable int id) {
        userService.deletarUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }



}
