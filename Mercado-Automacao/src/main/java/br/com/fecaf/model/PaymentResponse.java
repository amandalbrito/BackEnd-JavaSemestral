package br.com.fecaf.model;

public class PaymentResponse {
    private String paymentIntentId;
    private long amountInCents;
    private String amountFormatted;

    public PaymentResponse(String paymentIntentId, long amountInCents, String amountFormatted) {
        this.paymentIntentId = paymentIntentId;
        this.amountInCents = amountInCents;
        this.amountFormatted = amountFormatted;
    }

    // Getters e setters
    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getAmountFormatted() {
        return amountFormatted;
    }

    public void setAmountFormatted(String amountFormatted) {
        this.amountFormatted = amountFormatted;
    }
}
