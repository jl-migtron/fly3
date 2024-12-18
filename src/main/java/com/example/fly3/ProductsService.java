package com.example.fly3;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Service
public class ProductsService {

    Logger logger = LoggerFactory.getLogger(ProductsService.class);

    @Autowired
    ProductsRepo productsRepo;

    // get all products in repo
    public List<Product> getAllProducts() {
        return productsRepo.findAll();
    }

    // get product with given id
    public Product getProductById(Long id) {
        return productsRepo.findById(id).orElseThrow();
    }

    // get product with given category
    public List<Product> getProductsByCategory(Category cat) {
        return productsRepo.findByCategory(cat);
    }

    // create a product
    public Product createProduct(Product product) {
        try {
            Product product2 = productsRepo.save(product);
            logger.info("Created product " + product2.getId().toString());
            return product2;
        } catch (Exception ex) {
            logger.error("Failed product creation", ex);
            return null;
        }
    }

    // update product with given id
    public Product updateProduct(Long id, Product product) {
        try {
            Product product2 = productsRepo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

            product2.setName(product.getName());
            product2.setPrice(product.getPrice());
            product2.setCategory(product.getCategory());
            product2.setImage(product.getImage());
            Product product3 = productsRepo.save(product2);
            logger.info("Updated product " + id);
            return product3;
        } catch (Exception ex) {
            logger.error("Failed product update", ex);
            return null;
        }
    }

    // delete product with given id
    public boolean deleteProduct(Long id) {
        try {
            Optional<Product> product2 = productsRepo.findById(id);

            product2.ifPresentOrElse(cat2 -> {
                productsRepo.deleteById(id);
                logger.info("Deleted product " + id);
            },
                () -> logger.warn("Failed product delete: no product found with id " + id));

            return product2.isPresent();
        } catch (Exception ex) {
            logger.error("Failed product delete", ex);
            return false;
        }
    }

    // delete all products in repo
    public void deleteAll() {
        productsRepo.deleteAll();
        logger.info("All products deleted");
    }
}
