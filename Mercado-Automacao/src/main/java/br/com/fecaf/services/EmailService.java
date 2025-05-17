package br.com.fecaf.services;

import br.com.fecaf.model.*;
import br.com.fecaf.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    //Integra com o Banco de Dados
    private final CadastroRepository cadastroRepository;
    private final CartRepository cartRepository;

    @Autowired
    private JavaMailSender mailSender;
    private final String fromEmail;


    @Autowired
    public EmailService(JavaMailSender mailSender, CadastroRepository cadastroRepository, @Value("${spring.mail.from}") String fromEmail, CartRepository cartRepository) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.cadastroRepository = cadastroRepository;
        this.cartRepository = cartRepository;

    }


    //Rota do Email
    public String enviarEmail(String destinatario, int pessoa) {

        // Busca o usuário no banco de dados
        Cadastro cadastro = cadastroRepository.findByEmail(destinatario).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        Cart cart = cartRepository.findByUserId(pessoa).orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));


        // Cria o email usando o email do usuário
        Email email = new Email(cadastro.getEmail(), "Agradecemos por comprar conosco!",
                "Agradecemos por sua compra! \nAbaixo você consegue verificar alista dos itens comprados:"
                        + "\n     Produto" + "\n     " +cart.getCartItems()
                        +"     " + cart.calcularTotal()
        );


        //Verifica e impede o e-mail do destintário nulo
        if (email.getTo() == null || email.getTo().isEmpty()) {
            throw new IllegalArgumentException("O endereço de E-mail não pode ser vazio");

        }


        //Configurações do E-mail
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(fromEmail);
        mensagem.setTo(email.to());
        mensagem.setSubject(email.subject());
        mensagem.setText(email.body());

        //Envio do E-mail
        try {
            mailSender.send(mensagem);
            return "Email enviado com sucesso!";
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
        }
    }

}
