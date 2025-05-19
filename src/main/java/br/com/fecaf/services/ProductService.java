package br.com.fecaf.services;

import br.com.fecaf.model.Product;
import br.com.fecaf.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    public Product getProductByCodigoBarras(String codigoBarras) {
        return productRepository.findByCodigoBarras(codigoBarras);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}