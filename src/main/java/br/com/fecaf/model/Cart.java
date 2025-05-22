package br.com.fecaf.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // nullable false garante integridade
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "is_finalizado", nullable = false)
    private boolean finalizado = false;  // já inicializa para evitar null

    // Construtores
    public Cart() {}

    public Cart(User user) {
        this.user = user;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public void adicionarItem(Product product, int quantidade) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantidade);
                return;
            }
        }

        CartItem novoItem = new CartItem();
        novoItem.setProduct(product);
        novoItem.setQuantity(quantidade);
        novoItem.setCart(this);  // Relacionamento bidirecional
        cartItems.add(novoItem);
    }

    public void removerItem(int productId) {
        cartItems.removeIf(item -> item.getProduct().getId() == productId);
    }

    public void atualizarQuantidade(int productId, int novaQuantidade) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(novaQuantidade);
                return;
            }
        }
    }

    public double calcularTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPreco() * item.getQuantity();
        }
        return total;
    }

    public void limparCarrinho() {
        cartItems.clear();
    }
}
