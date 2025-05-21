package br.com.fecaf.dto;

public class PaymentResponse {

    private String paymentIntentId;
    private String clientSecret;
    private Long amount; // centavos
    private String formattedAmount;

    public PaymentResponse(String paymentIntentId, String clientSecret, Long amount, String formattedAmount) {
        this.paymentIntentId = paymentIntentId;
        this.clientSecret = clientSecret;
        this.amount = amount;
        this.formattedAmount = formattedAmount;
    }

    // getters e setters
    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount = formattedAmount;
    }
}
