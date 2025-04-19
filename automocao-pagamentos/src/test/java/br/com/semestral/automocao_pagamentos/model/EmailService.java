package br.com.semestral.automocao_pagamentos.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
   
   //Email de Confirmação de Compra
   private final JavaMailSender mailSender;
   private final String fromEmail;

   public EmailService(JavaMailSender mailSender, @Value("${spring.mail.from}") String fromEmail){
      this.mailSender = mailSender;
      this.fromEmail = fromEmail;
   }

   //Rota do Email
   public void enviarEmail(Email email){

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
      mailSender.send(mensagem);
   }

}
