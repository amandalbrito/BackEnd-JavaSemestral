package br.com.fecaf.services;

import br.com.fecaf.dto.PaymentResponse;
import br.com.fecaf.model.Cart;
import br.com.fecaf.model.Product;
import br.com.fecaf.model.User;
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

    // Injetar PaymentService para usar no finalizarCompra
    @Autowired
    private PaymentService paymentService;

    public Cart getCartByUserId(int userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        return cartOptional.orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public Cart adicionarItem(int userId, int productId, int quantidade) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        cart.adicionarItem(product, quantidade);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removerItem(int userId, int productId) {
        Cart cart = getCartByUserId(userId);
        cart.removerItem(productId);
        return cartRepository.save(cart);
    }

    public double calcularTotal(int userId) {
        Cart cart = getCartByUserId(userId);
        return cart.calcularTotal();
    }

    @Transactional
    public void limparCarrinho(int userId) {
        Cart cart = getCartByUserId(userId);
        cart.limparCarrinho();
        cartRepository.save(cart);
    }

    @Transactional
    public Cart atualizarQuantidade(int userId, int productId, int novaQuantidade) {
        Cart cart = getCartByUserId(userId);
        cart.atualizarQuantidade(productId, novaQuantidade);
        return cartRepository.save(cart);
    }

    @Transactional
    public PaymentResponse finalizarCompra(int userId) throws StripeException {
        Cart cart = getCartByUserId(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Carrinho vazio, não é possível finalizar compra.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Calcular total em centavos (considerando que preço está em double em reais)
        long totalCentavos = Math.round(cart.calcularTotal() * 100);

        // Marcar carrinho como finalizado
        cart.setFinalizado(true);
        cartRepository.save(cart);

        // Criar pagamento e salvar no banco via PaymentService
        String descricaoPagamento = "Pagamento do carrinho do usuário " + userId;

        return paymentService.createPaymentResponse(totalCentavos, "brl", descricaoPagamento, user, cart);
    }
}
