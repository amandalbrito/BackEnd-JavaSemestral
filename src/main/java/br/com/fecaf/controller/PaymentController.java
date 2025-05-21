package br.com.fecaf.controller;

import br.com.fecaf.dto.PaymentResponse;
import br.com.fecaf.model.*;
import br.com.fecaf.repository.CartRepository;
import br.com.fecaf.repository.PaymentRepository;
import br.com.fecaf.services.CartService;
import br.com.fecaf.services.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @PostMapping("/intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@RequestParam Long userId) {
        Optional<Cart> optionalCarrinho = cartRepository.findByUserId(userId.intValue());

        if (optionalCarrinho == null || optionalCarrinho.get().getCartItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaymentIntentResponse("Carrinho não encontrado ou vazio"));
        }

        long total = optionalCarrinho.get().getCartItems().stream()
                .mapToLong(item -> (long) (item.getProduct().getPreco() * item.getQuantity() * 100))
                .sum();

        try {
            PaymentIntent intent = paymentService.createPaymentIntent(
                    total,
                    "brl",
                    "Pagamento de produtos do carrinho"
            );

            PaymentEntity paymentEntity = new PaymentEntity();
            paymentEntity.setAmount(total);
            paymentEntity.setCurrency("brl");
            paymentEntity.setDescription("Pagamento de produtos do carrinho");
            paymentEntity.setStripePaymentId(intent.getId());
            paymentEntity.setCreatedAt(LocalDateTime.now());

            paymentRepository.save(paymentEntity);

            return ResponseEntity.ok(new PaymentIntentResponse(intent.getClientSecret()));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentIntentResponse("Erro ao criar PaymentIntent: " + e.getMessage()));
        }
    }

    @PostMapping("/customer")
    public Customer criarConta(@RequestBody User user) throws StripeException {
        return paymentService.criarUsuario(user.getEmail(), user.getNome());
    }

    @PostMapping("/checkout")
    public PaymentResponse createPaymentResponse(@RequestBody Payment payment) throws StripeException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Cart cart = cartService.getCartByUserId(user.getId());

        return paymentService.createPaymentResponse(
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                user,
                cart
        );
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmarPagamento(@RequestParam String paymentIntentId,
                                                     @RequestParam String destinatario,
                                                     @RequestParam Long pessoa) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                RestTemplate restTemplate = new RestTemplate();
                String url = String.format("http://localhost:8080/email/enviarEmail?destinatario=%s&pessoa=%d", destinatario, pessoa);
                restTemplate.postForObject(url, null, String.class);

                return ResponseEntity.ok("Pagamento confirmado e e-mail enviado.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Pagamento ainda não confirmado. Status atual: " + paymentIntent.getStatus());
            }

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar o pagamento: " + e.getMessage());
        }
    }
}
