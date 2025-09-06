package br.com.fecaf.controller;

import br.com.fecaf.dto.AddCartRequest;
import br.com.fecaf.dto.PaymentResponse;
import br.com.fecaf.dto.QuantidadeRequest;
import br.com.fecaf.exception.UnauthorizedException;
import br.com.fecaf.model.Cart;
import br.com.fecaf.model.Product;
import br.com.fecaf.model.User;
import br.com.fecaf.repository.ProductRepository;
import br.com.fecaf.repository.UserRepository;
import br.com.fecaf.security.JwtUtil;
import br.com.fecaf.services.CartService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private int getUserId(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado."));
        }
        throw new UnauthorizedException("Token JWT ausente ou inválido.");
    }

    @GetMapping
    public Cart getCart(HttpServletRequest request) {
        try {
            int userId = getUserId(request);
            return cartService.getCartByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddCartRequest addRequest, HttpServletRequest request) {
        int userId = getUserId(request);
        String codigoBarras = addRequest.getCodigoBarras();
        System.out.println("Recebido código de barras: '" + codigoBarras + "'");

        Product product = productRepository.findByCodigoBarras(codigoBarras);

        if (product == null) {
            System.out.println("Produto não encontrado no banco para código: '" + codigoBarras + "'");
            return ResponseEntity.badRequest().body(null);
        }
        System.out.println("Produto encontrado: " + product.getNome());

        Cart updatedCart = cartService.adicionarItem(userId, product.getId(), addRequest.getQuantidade());
        return ResponseEntity.ok(updatedCart);
    }


    @PutMapping("/{productId}")
    public Cart updateQuantidade(
            @PathVariable int productId,
            @RequestBody QuantidadeRequest body,
            HttpServletRequest request) {
        int userId = getUserId(request);
        return cartService.atualizarQuantidade(userId, productId, body.getQuantidade());
    }

    @DeleteMapping("/{productId}")
    public Cart deleteFromCart(@PathVariable int productId, HttpServletRequest request) {
        int userId = getUserId(request);
        return cartService.removerItem(userId, productId);
    }

    @DeleteMapping("/clear")
    public void clearCart(HttpServletRequest request) {
        int userId = getUserId(request);
        cartService.limparCarrinho(userId);
    }

    @GetMapping("/total")
    public double calcularTotal(HttpServletRequest request) {
        int userId = getUserId(request);
        return cartService.calcularTotal(userId);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> finalizarCompra(HttpServletRequest request) {
        int userId = getUserId(request);

        if (userId == 0) {
            return ResponseEntity.status(401).body("Usuário não autorizado.");
        }

        try {
            PaymentResponse paymentResponse = cartService.finalizarCompra(userId);
            return ResponseEntity.ok(paymentResponse);
        } catch (RuntimeException | StripeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
