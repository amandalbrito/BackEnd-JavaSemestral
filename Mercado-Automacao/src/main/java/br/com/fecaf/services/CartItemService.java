package br.com.fecaf.services;

import br.com.fecaf.model.CartItem;
import br.com.fecaf.model.User;
import br.com.fecaf.model.Product;
import br.com.fecaf.repository.CartItemRepository;
import br.com.fecaf.repository.UserRepository;
import br.com.fecaf.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public CartItem addToCart(int userId, int productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            return cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            return cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getUserCart(int userId) {
        return cartItemRepository.findByUserId(userId);
    }
}
