package com.example.fly3.rest;

import com.example.fly3.model.Product;
import com.example.fly3.services.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@RestController
@RequestMapping("/api/categories")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{catId}/products")
    public ResponseEntity<List<Product>> getAllProductsForCategory(@PathVariable Long catId) {
        List<Product> products = productService.getProductsByCategory(catId);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok().body(product);
    }

    @PostMapping("/{catId}/products")
    public ResponseEntity<Product> createProduct(@PathVariable Long catId, @RequestBody Product product) {
        Product prod = productService.createProduct(catId, product);
        return ResponseEntity.ok(prod);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product prod = productService.updateProduct(id, product);
        return ResponseEntity.ok(prod);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
