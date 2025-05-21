package br.com.fecaf.services;

import br.com.fecaf.dto.PaymentResponse;
import br.com.fecaf.model.Cart;
import br.com.fecaf.model.PaymentEntity;
import br.com.fecaf.model.User;
import br.com.fecaf.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(
            @Value("${stripe.apiKey}") String apiKey,
            PaymentRepository paymentRepository
    ) {
        Stripe.apiKey = apiKey;
        this.paymentRepository = paymentRepository;
    }

    public PaymentIntent createPaymentIntent(Long amount, String currency, String description) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description)
                .build();

        return PaymentIntent.create(params);
    }

    public PaymentResponse createPaymentResponse(Long amount, String currency, String description, User user, Cart cart) throws StripeException {
        // Criar PaymentIntent Stripe
        PaymentIntent intent = createPaymentIntent(amount, currency, description);

        // Salvar PaymentEntity vinculando user e cart
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setDescription(description);
        payment.setStripePaymentId(intent.getId());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUser(user);
        payment.setCart(cart);

        paymentRepository.save(payment);

        String formattedAmount = convertCentavosToReais(amount);

        // Retornando DTO com paymentIntentId e clientSecret
        return new PaymentResponse(
                intent.getId(),
                intent.getClientSecret(),
                amount,
                formattedAmount
        );
    }

    private String convertCentavosToReais(Long centavos) {
        double reais = centavos / 100.0;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormatter.format(reais);
    }

    public Customer criarUsuario(String email, String nome) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(nome)
                .build();
        return Customer.create(params);
    }
}
