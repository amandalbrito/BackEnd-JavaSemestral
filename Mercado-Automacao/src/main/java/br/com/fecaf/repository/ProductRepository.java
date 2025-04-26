package br.com.fecaf.repository;

import br.com.fecaf.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByCodigoBarras(String codigoBarras);
}