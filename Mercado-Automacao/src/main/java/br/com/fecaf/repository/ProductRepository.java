package br.com.fecaf.repository;
import br.com.fecaf.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByCodigoBarras(String codigoBarras);  // retorna Optional<Product>
}