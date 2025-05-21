package br.com.fecaf.model;

public class PaymentIntentResponse {

    private String clientSecret;
    private String message;

    public PaymentIntentResponse(String clientSecret) {
        this.clientSecret = clientSecret;
        this.message = null;
    }

    public PaymentIntentResponse(String message, boolean isError) {
        this.message = message;
        this.clientSecret = null;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
