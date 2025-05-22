package br.com.fecaf.services;

import br.com.fecaf.model.Cart;
import br.com.fecaf.model.CartItem;
import br.com.fecaf.model.Product;
import br.com.fecaf.model.User;
import br.com.fecaf.repository.CartItemRepository;
import br.com.fecaf.repository.CartRepository;
import br.com.fecaf.repository.ProductRepository;
import br.com.fecaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public CartItem addToCart(int userId, int productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        Cart cart;

        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        Optional<CartItem> optionalItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (optionalItem.isPresent()) {
            CartItem existingItem = optionalItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            return cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            return cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getCartItemsByUserId(int userId) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            return cartItemRepository.findByCartId(cart.getId());
        }
        return List.of(); // Lista vazia se o carrinho não existir
    }

    public void removeItemFromCart(int cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
