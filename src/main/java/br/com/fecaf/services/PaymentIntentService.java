package br.com.fecaf.services;

import br.com.fecaf.model.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class PaymentIntentService {

    @Value("${stripe.apiKey}")
    private String apiKey;

    public PaymentResponse createPaymentIntent(long amount) throws StripeException {
        Stripe.apiKey = apiKey;

        System.out.println("Criando pagamento no valor de: " + convertCentavosToReais(amount));

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount); // valor em centavos
        params.put("currency", "brl");
        params.put("payment_method_types", Arrays.asList("card"));

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        String formattedAmount = convertCentavosToReais(amount);

        return new PaymentResponse(
                paymentIntent.getId(),
                amount,
                formattedAmount
        );
    }

    private String convertCentavosToReais(long centavos) {
        double reais = centavos / 100.0;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormatter.format(reais);
    }
}
