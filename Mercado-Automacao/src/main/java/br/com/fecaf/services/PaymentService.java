package br.com.fecaf.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    //Configura apiKey do Stripe
    public PaymentService (@Value("${stripe.apiKey}") String apiKey){

        Stripe.apiKey = apiKey;

    }

    //Cria e armazena dados de Intenção de Pagamento
    public PaymentIntent createPaymentIntent(Long amount, String currency, String description) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description)
                .build();

        return PaymentIntent.create(params);
    }

    //Cria uma conta para o Cliente no Stripe
    public Customer criarUsuario(String email, String nome) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(nome)
                .build();
        return Customer.create(params);
    }


}
