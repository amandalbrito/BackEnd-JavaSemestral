package br.com.fecaf.controller;

import br.com.fecaf.dto.PaymentResponse;
import br.com.fecaf.model.*;
import br.com.fecaf.repository.*;
import br.com.fecaf.security.JwtUtil;
import br.com.fecaf.services.*;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/payment")
public class StripePaymentController {

    private static final String DEFAULT_CURRENCY = "brl";

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        com.stripe.Stripe.apiKey = stripeApiKey;
    }

    private User getAuthenticatedUser(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.getEmailFromToken(token);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou usuário não autorizado", e);
        }
    }

    @PostMapping("/intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestHeader("Authorization") String authHeader) throws StripeException {
        User user = getAuthenticatedUser(authHeader);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrinho não encontrado"));

        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carrinho está vazio");
        }

        long amount = cart.getCartItems().stream()
                .mapToLong(item -> (long) (item.getProduct().getPreco() * 100) * item.getQuantity())
                .sum();

        String description = "Pagamento de produtos do carrinho";

        PaymentIntent intent = paymentService.createPaymentIntent(amount, DEFAULT_CURRENCY, description);

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setUser(user);
        paymentEntity.setAmount(amount);
        paymentEntity.setCurrency(DEFAULT_CURRENCY);
        paymentEntity.setDescription(description);
        paymentEntity.setStripePaymentId(intent.getId());
        paymentEntity.setStatus(PaymentEntity.PaymentStatus.fromStripeStatus(intent.getStatus()));
        paymentEntity.setCreatedAt(LocalDateTime.now());
        paymentEntity.setCart(cart);

        paymentRepository.save(paymentEntity);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmarPagamento(@RequestHeader("Authorization") String authHeader,
                                                     @RequestParam String paymentIntentId,
                                                     @RequestParam int userIdToNotify) throws StripeException {
        User user = getAuthenticatedUser(authHeader);

        PaymentIntent paymentIntent = paymentService.retrievePaymentIntent(paymentIntentId);

        if ("succeeded".equals(paymentIntent.getStatus())) {
            PaymentEntity payment = paymentRepository.findByStripePaymentId(paymentIntentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));

            if (!Objects.equals(payment.getUser().getId(), user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pagamento não pertence ao usuário autenticado");
            }

            payment.setStatus(PaymentEntity.PaymentStatus.fromStripeStatus(paymentIntent.getStatus()));
            paymentRepository.save(payment);

            String mensagem = emailService.enviarEmail(userIdToNotify);
            return ResponseEntity.ok(mensagem);
        } else {
            return ResponseEntity.badRequest().body("Pagamento não confirmado. Status atual: " + paymentIntent.getStatus());
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

        long expectedAmount = cart.getCartItems().stream()
                .mapToLong(item -> (long) (item.getProduct().getPreco() * 100) * item.getQuantity())
                .sum();

        if (payment.getAmount() != expectedAmount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do pagamento inválido");
        }

        if (!DEFAULT_CURRENCY.equals(payment.getCurrency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Moeda inválida");
        }

        return paymentService.createPaymentResponse(
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                user,
                cart
        );
    }
}
