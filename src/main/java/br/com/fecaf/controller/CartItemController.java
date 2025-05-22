package br.com.fecaf.controller;

import br.com.fecaf.model.CartItem;
import br.com.fecaf.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @PostMapping("/add/{userId}/{productId}")
    public ResponseEntity<CartItem> addToCart(@PathVariable int userId, @PathVariable int productId) {
        try {
            CartItem cartItem = cartItemService.addToCart(userId, productId);
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getUserCart(@PathVariable int userId) {
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cartItems);
    }
}
