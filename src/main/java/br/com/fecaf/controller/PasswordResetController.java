package br.com.fecaf.controller;

import br.com.fecaf.dto.PasswordResetRequest;
import br.com.fecaf.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final UserService userService;

    public PasswordResetController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        boolean result = userService.resetPassword(
                request.getEmail(),
                request.getVerificationCode(),
                request.getNewPassword()
        );

        if (result) {
            return ResponseEntity.ok("Senha alterada com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Falha na alteração da senha. Verifique os dados.");
        }
    }
}
