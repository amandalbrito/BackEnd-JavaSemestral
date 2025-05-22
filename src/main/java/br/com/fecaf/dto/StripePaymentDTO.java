package br.com.fecaf.dto;

public class StripePaymentDTO {
    private long amount;
    private String currency;
    private String description;

    // Getters e Setters
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
