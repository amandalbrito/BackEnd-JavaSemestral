package br.com.fecaf.services;

import br.com.fecaf.model.*;
import br.com.fecaf.repository.CartRepository;
import br.com.fecaf.repository.PaymentRepository;
import br.com.fecaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    @Autowired
    public EmailService(JavaMailSender mailSender,
                        UserRepository userRepository,
                        PaymentRepository paymentRepository,
                        CartRepository cartRepository,
                        @Value("${spring.mail.from}") String fromEmail) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.fromEmail = fromEmail;
    }

    public String enviarEmail(int userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        PaymentEntity payment = paymentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));

        StringBuilder itensComprados = new StringBuilder();
        double totalCalculado = 0.0;

        for (CartItem item : cart.getCartItems()) {
            String nomeProduto = item.getProduct().getNome();
            int quantidade = item.getQuantity();
            double preco = item.getProduct().getPreco();
            double subtotal = quantidade * preco;
            totalCalculado += subtotal;

            itensComprados.append("\n- ")
                    .append(nomeProduto)
                    .append(" | Quantidade: ").append(quantidade)
                    .append(" | Preço unitário: R$").append(String.format("%.2f", preco))
                    .append(" | Subtotal: R$").append(String.format("%.2f", subtotal));
        }

        String corpoEmail = String.format("""
            Olá %s,
            
            Obrigada pela sua compra no Fila Free!!
            
            Detalhes do pedido:
            Email do cliente: %s
            Valor total pago: R$%.2f
            Moeda: %s
            Data da compra: %s
            
            Produtos adquiridos: %s
            
            Total calculado: R$%.2f
            
            Agradecemos pela preferência!
            """,
                user.getNome(),
                user.getEmail(),
                payment.getAmount() / 100.0,
                payment.getCurrency(),
                payment.getCreatedAt(),
                itensComprados.toString(),
                totalCalculado
        );

        // Envia o e-mail
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(fromEmail);
        mensagem.setTo(user.getEmail());
        mensagem.setSubject("Confirmação de Compra - Fila Free");
        mensagem.setText(corpoEmail);

        try {
            mailSender.send(mensagem);
            return "E-mail enviado com sucesso!";
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}