package br.com.fecaf.services;

import br.com.fecaf.dto.PaymentResponse;
import br.com.fecaf.model.Cart;
import br.com.fecaf.model.CartItem;
import br.com.fecaf.model.Product;
import br.com.fecaf.model.User;
import br.com.fecaf.repository.CartItemRepository;
import br.com.fecaf.repository.CartRepository;
import br.com.fecaf.repository.ProductRepository;
import br.com.fecaf.repository.UserRepository;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PaymentService paymentService;

    public Cart getCartByUserId(int userId) {
        return cartRepository.findByUserIdAndFinalizadoFalse(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart adicionarItem(int userId, int productId, int quantidade) {
        Cart cart = getCartByUserId(userId);
        Optional<CartItem> opt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (opt.isPresent()) {
            CartItem item = opt.get();
            item.setQuantity(item.getQuantity() + quantidade);
            cartItemRepository.save(item);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantidade);
            cartItemRepository.save(newItem);
        }
        return cartRepository.findById(cart.getId())
                .orElseThrow();
    }



    @Transactional
    public Cart removerItem(int userId, int productId) {
        Cart cart = getCartByUserId(userId);
        cart.removerItem(productId);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart atualizarQuantidade(int userId, int productId, int quantidade) {
        Cart cart = getCartByUserId(userId);
        cart.atualizarQuantidade(productId, quantidade);
        return cartRepository.save(cart);
    }

    @Transactional
    public void limparCarrinho(int userId) {
        Cart cart = getCartByUserId(userId);
        cart.limparCarrinho();
        cartRepository.save(cart);
    }

    public double calcularTotal(int userId) {
        return getCartByUserId(userId).calcularTotal();
    }

    @Transactional
    public PaymentResponse finalizarCompra(int userId) throws StripeException {
        Cart cart = getCartByUserId(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Carrinho vazio, não é possível finalizar compra.");
        }

        long total = Math.round(cart.calcularTotal() * 100);
        cart.setFinalizado(true);
        cartRepository.save(cart);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        String desc = "Pagamento do carrinho do usuário " + userId;
        return paymentService.createPaymentResponse(total, "brl", desc, user, cart);
    }
}
