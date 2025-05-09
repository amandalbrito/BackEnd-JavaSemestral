package br.com.fecaf.services;

import br.com.fecaf.model.Email;
import br.com.fecaf.model.User;
import br.com.fecaf.repository.CartItemRepository;
import br.com.fecaf.repository.UserRepository;
import br.com.fecaf.model.CartItem;
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

    //Integra com o Banco de Dados
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    private JavaMailSender mailSender;
    private final String fromEmail;


    @Autowired
    public EmailService(JavaMailSender mailSender, UserRepository userRepository, @Value("${spring.mail.from}") String fromEmail, CartItemRepository cartItemRepository) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;

    }


    //Rota do Email
    public String enviarEmail(String destinatario, int carrinho, int pessoa) {

        // Busca o usuário no banco de dados
        User user = userRepository.findByEmail(destinatario).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(carrinho, pessoa);


        // Cria o email usando o email do usuário
        Email email = new Email(user.getEmail(), "Agradecemos por comprar conosco!",
                "Agradecemos por sua compra! \nAbaixo você consegue verificar alista dos itens comprados:"
                        + "\n     Produto" + "\n     " +cartItem.getProduct()
                        +"     " + cartItem.getQuantity());


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
