package br.com.fecaf.controller;

import br.com.fecaf.model.Cart;
import br.com.fecaf.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Cart adicionarItem(
            @RequestParam int userId,
            @RequestParam int productId,
            @RequestParam int quantidade) {
        return cartService.adicionarItem(userId, productId, quantidade);
    }

    @DeleteMapping("/remove")
    public Cart removerItem(
            @RequestParam int userId,
            @RequestParam int productId) {
        return cartService.removerItem(userId, productId);
    }

    @PutMapping("/update")
    public Cart atualizarQuantidade(
            @RequestParam int userId,
            @RequestParam int productId,
            @RequestParam int quantidade) {
        return cartService.atualizarQuantidade(userId, productId, quantidade);
    }

    @GetMapping
    public Cart getCart(@RequestParam int userId) {
        return cartService.getCartByUserId(userId);
    }

    @GetMapping("/total")
    public double calcularTotal(@RequestParam int userId) {
        return cartService.calcularTotal(userId);
    }

    @DeleteMapping("/clear")
    public void limparCarrinho(@RequestParam int userId) {
        cartService.limparCarrinho(userId);
    }
}