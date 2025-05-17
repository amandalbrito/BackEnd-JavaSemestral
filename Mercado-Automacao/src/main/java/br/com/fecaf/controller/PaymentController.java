package br.com.fecaf.controller;

import br.com.fecaf.model.PaymentIntentResponse;
import br.com.fecaf.model.User;
import br.com.fecaf.model.Payment;
import br.com.fecaf.model.PaymentEntity;
import br.com.fecaf.repository.PaymentRepository;
import br.com.fecaf.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500/")
@RequestMapping("/api/stripe/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;  // INJEÇÃO do repositório

    @PostMapping("/create-payment-intent")
    public PaymentIntentResponse createPaymentIntent(@RequestBody Payment payment) throws StripeException {
        PaymentIntent intent = paymentService.createPaymentIntent(
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        );

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setAmount(payment.getAmount());
        paymentEntity.setCurrency(payment.getCurrency());
        paymentEntity.setDescription(payment.getDescription());
        paymentEntity.setStripePaymentId(intent.getId());
        paymentEntity.setCreatedAt(LocalDateTime.now());

        // salva no banco de dados
        paymentRepository.save(paymentEntity);

        return new PaymentIntentResponse(intent.getClientSecret());
    }

    @PostMapping("/create-customer")
    public Customer criarConta(@RequestBody User user) throws StripeException {
        return paymentService.criarUsuario(user.getEmail(), user.getNome());
    }
}
