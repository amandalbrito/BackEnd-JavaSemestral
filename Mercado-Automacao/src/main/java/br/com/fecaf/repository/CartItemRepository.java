package br.com.fecaf.repository;

import br.com.fecaf.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(int userId);
    CartItem findByUserIdAndProductId(int userId, int productId);
}
