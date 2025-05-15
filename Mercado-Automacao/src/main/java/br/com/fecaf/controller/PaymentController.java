package br.com.fecaf.controller;

import br.com.fecaf.model.PaymentIntentResponse;
import br.com.fecaf.model.User;
import br.com.fecaf.model.Payment;
import br.com.fecaf.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500/")
@RequestMapping("/api/stripe/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public PaymentIntentResponse createPaymentIntent(@RequestBody Payment payment) throws StripeException {
        PaymentIntent intent = paymentService.createPaymentIntent(
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        );

        return new PaymentIntentResponse(intent.getClientSecret());
    }

    @PostMapping("/create-customer")
    public Customer criarConta(@RequestBody User user) throws StripeException {
        return paymentService.criarUsuario(user.getEmail(), user.getNome());
    }

}
