package br.com.fecaf.controller;


import br.com.fecaf.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("Acessou o Get");
        return "Api no Ar funcionando ...";

    }


    @PostMapping("/enviarEmail")
    public ResponseEntity<String> enviarEmail(@RequestParam int pessoa) {
        try {
            String mensagem = emailService.enviarEmail(pessoa);
            return ResponseEntity.ok(mensagem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a requisição: " + e.getMessage());
        }
    }

}
