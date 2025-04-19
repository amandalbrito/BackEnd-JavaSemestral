package br.com.semestral.automacao_pagamentos.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.semestral.automacao_pagamentos.model.Email;
import br.com.semestral.automacao_pagamentos.model.EmailService;

@RestController
@RequestMapping("email")
public class EmailController {

   private EmailService emailService;


   public EmailController(EmailService emailService) {
      this.emailService = emailService;
   }



   @PostMapping
   public void enviarEmail(@RequestBody Email email){
      emailService.enviarEmail(email);
   }

}
