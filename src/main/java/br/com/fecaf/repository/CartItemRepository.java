package br.com.fecaf.repository;

import br.com.fecaf.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartId(int cartId);
    Optional<CartItem> findByCartIdAndProductId(int cartId, int productId);
}
