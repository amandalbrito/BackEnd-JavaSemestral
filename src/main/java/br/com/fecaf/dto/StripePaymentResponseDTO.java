package br.com.fecaf.dto;

public class StripePaymentResponseDTO {
    private String amountFormatted;

    public StripePaymentResponseDTO(String amountFormatted) {
        this.amountFormatted = amountFormatted;
    }

    public String getAmountFormatted() {
        return amountFormatted;
    }

    public void setAmountFormatted(String amountFormatted) {
        this.amountFormatted = amountFormatted;
    }
}
