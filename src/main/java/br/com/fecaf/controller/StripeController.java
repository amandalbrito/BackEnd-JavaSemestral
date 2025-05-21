    package br.com.fecaf.controller;

    import br.com.fecaf.model.Cart;
    import br.com.fecaf.model.PaymentEntity;
    import br.com.fecaf.model.User;
    import br.com.fecaf.repository.CartRepository;
    import br.com.fecaf.repository.PaymentRepository;
    import br.com.fecaf.repository.UserRepository;
    import br.com.fecaf.security.JwtUtil;
    import br.com.fecaf.services.EmailService;
    import br.com.fecaf.services.StripeService;
    import br.com.fecaf.services.CartService;
    import com.stripe.exception.StripeException;
    import com.stripe.model.PaymentIntent;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.server.ResponseStatusException;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/stripe")
    public class StripeController {

        @Autowired
        private StripeService stripeService;

        @Autowired
        private PaymentRepository paymentRepository;

        @Autowired
        private EmailService emailService;

        @Autowired
        private CartService cartService;

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JwtUtil jwtUtil;  // Componente para manipular JWT

        @PostMapping("/create-payment-intent")
        public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestHeader("Authorization") String authHeader) throws StripeException {
            try {
                // Extrair token do header Authorization (esperado "Bearer <token>")
                String token = authHeader.replace("Bearer ", "");

                // Pegar email do token JWT
                String email = jwtUtil.getEmailFromToken(token);

                // Buscar usuário pelo email do token
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

                // Buscar carrinho do usuário
                Cart cart = cartRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrinho não encontrado"));

                // Somar total do carrinho em centavos (preço em reais * 100)
                long amount = cart.getCartItems().stream()
                        .mapToLong(item -> (long) (item.getProduct().getPreco() * 100) * item.getQuantity())
                        .sum();

                String currency = "brl";
                String description = "Pagamento de produtos do carrinho";

                PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, currency, description);

                // Criar e salvar PaymentEntity
                PaymentEntity payment = new PaymentEntity();
                payment.setUser(user);
                payment.setStripePaymentId(paymentIntent.getId());
                payment.setAmount(amount);
                payment.setCurrency(currency);
                payment.setDescription(description);
                payment.setStatus(PaymentEntity.PaymentStatus.fromStripeStatus(paymentIntent.getStatus()));
                payment.setCreatedAt(LocalDateTime.now());
                payment.setCart(cart);
                paymentRepository.save(payment);

                Map<String, String> response = new HashMap<>();
                response.put("clientSecret", paymentIntent.getClientSecret());

                return ResponseEntity.ok(response);

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou usuário não autorizado", e);
            }
        }

        @PostMapping("/confirmar-pagamento")
        public ResponseEntity<String> confirmarPagamento(@RequestHeader("Authorization") String authHeader,
                                                         @RequestParam String paymentIntentId,
                                                         @RequestParam int pessoa) throws StripeException {
            try {
                String token = authHeader.replace("Bearer ", "");
                String email = jwtUtil.getEmailFromToken(token);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

                // Recuperar PaymentIntent da Stripe
                PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

                if ("succeeded".equals(paymentIntent.getStatus())) {
                    // Buscar pagamento no banco garantindo que pertença ao usuário logado
                    PaymentEntity payment = paymentRepository.findByStripePaymentId(paymentIntentId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));

                    if (payment.getUser().getId() != user.getId()) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pagamento não pertence ao usuário autenticado");
                    }

                    // Converter e setar o status usando o enum
                    payment.setStatus(PaymentEntity.PaymentStatus.fromStripeStatus(paymentIntent.getStatus()));
                    paymentRepository.save(payment);

                    // Enviar e-mail para a pessoa informada
                    String mensagem = emailService.enviarEmail(pessoa);

                    return ResponseEntity.ok(mensagem);

                } else {
                    return ResponseEntity.badRequest().body("Pagamento não confirmado.");
                }

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou usuário não autorizado", e);
            }
        }

    }