package br.com.fecaf.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_payments")
public class PaymentEntity {

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED;

        public static PaymentStatus fromStripeStatus(String stripeStatus) {
            if (stripeStatus == null) return PENDING;
            switch (stripeStatus.toLowerCase()) {
                case "succeeded":
                    return COMPLETED;
                case "pending":
                    return PENDING;
                case "failed":
                    return FAILED;
                case "canceled":
                case "cancelled":
                    return CANCELLED;
                default:
                    return PENDING;
            }
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    private String currency;
    private String description;
    private String stripePaymentId;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStripePaymentId() { return stripePaymentId; }
    public void setStripePaymentId(String stripePaymentId) { this.stripePaymentId = stripePaymentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
}
