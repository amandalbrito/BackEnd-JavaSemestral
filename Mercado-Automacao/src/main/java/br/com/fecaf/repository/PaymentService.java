package br.com.fecaf.repository;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.apiKey}")
    private String apiKey;

    public PaymentIntent createPaymentIntent(long amount) throws StripeException {
        Stripe.apiKey = apiKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount); // valor em centavos
        params.put("currency", "usd"); // ou sua moeda
        params.put("payment_method_types", Arrays.asList("card"));

        return PaymentIntent.create(params);
    }


}
