package br.com.fecaf.repository;

import br.com.fecaf.model.Cart;
import br.com.fecaf.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @EntityGraph(attributePaths = {"cartItems", "cartItems.product"})
    Optional<Cart> findByUserIdAndFinalizadoFalse(int userId);

    @EntityGraph(attributePaths = {"cartItems", "cartItems.product"})
    Optional<Cart> findById(int id);

    // mantém a query original se for usada em outro serviço:
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    @EntityGraph(attributePaths = {"cartItems","cartItems.product"})
    Optional<Cart> findByUserId(@Param("userId") int userId);
}