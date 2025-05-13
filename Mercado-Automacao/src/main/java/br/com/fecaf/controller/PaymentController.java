package br.com.fecaf.controller;

import br.com.fecaf.repository.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public String createPayment(@RequestBody Map<String, String> request) throws StripeException {
        long amount = Long.parseLong(request.get("amount")); // valor recebido do frontend
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(amount);
        return paymentIntent.getClientSecret();
    }


}
