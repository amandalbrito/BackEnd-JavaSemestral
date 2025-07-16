package br.com.fecaf.controller;

import br.com.fecaf.model.User;
import br.com.fecaf.services.AuthService;
import br.com.fecaf.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://filafree.netlify.app/", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String senha = loginData.get("senha");
            String token = authService.autenticar(email, senha);

            Optional<User> user = userService.pesquisarUser(email);

            User userLogin = user.get();


            return ResponseEntity.ok(Map.of("token", token, "email", email, "userId", userLogin.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }
}
