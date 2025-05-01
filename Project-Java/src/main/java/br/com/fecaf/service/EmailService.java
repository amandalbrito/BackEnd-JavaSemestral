package br.com.fecaf.service;

import br.com.fecaf.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    //Email de Confirmação de Compra
    @Autowired
    private JavaMailSender mailSender;

    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.from}") String fromEmail){
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    //Rota do Email
    public ResponseEntity<?> enviarEmail(Email email){

        //Verifica e impede o e-mail do destintário nulo
        if (email.getTo() == null || email.getTo().isEmpty()) {
            throw new IllegalArgumentException("O endereço de E-mail não pode ser vazio");

        }

        //Informações do destinatório e e-mail do remetente
        var mensagem = new SimpleMailMessage();
        mensagem.setFrom(fromEmail);
        mensagem.setTo(email.to());
        mensagem.setSubject(email.subject());
        mensagem.setText(email.body());

        try {
            mailSender.send(mensagem);
            return new ResponseEntity<>(
                "{\"message\": \"E-mail enviado com sucesso\"}",  // Retorno como uma string JSON
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "{\"message\": \"Erro ao enviar e-mail: " + e.getMessage() + "\"}",  // Retorno como uma string JSON com o erro
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
