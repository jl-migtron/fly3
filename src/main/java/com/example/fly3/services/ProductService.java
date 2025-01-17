package com.example.fly3.services;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.exceptions.ServiceException;
import com.example.fly3.model.Category;
import com.example.fly3.model.Product;
import com.example.fly3.repos.CategoryRepo;
import com.example.fly3.repos.ProductRepo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ProductRepo repo;
    @Autowired
    CategoryRepo categoryRepo;

    // create a product
    public Product createProduct(Long categoryId, Product product) {
        Category cat = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId.toString()));
        try {
            product.setCategory(cat);
            Product product2 = repo.save(product);
            logger.info("Created product " + product2);
            return product2;
        } catch (Exception ex) {
            logger.error("Failed product creation:", ex);
            return null;
        }
    }

    // get all products in repo
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    // get product with given id
    public Product getProductById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found " + id.toString()));
    }

    // get product with given category
    public List<Product> getProductsByCategory(Long catId) {
        return repo.findAllByCategory_Id(catId);
    }

    // update product with given id
    public Product updateProduct(Long id, Product product) {
        Product product2 = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found " + id.toString()));
        try {
            product2.setName(product.getName());
            product2.setPrice(product.getPrice());
            // category changes not allowed through product update
            product2.setImage(product.getImage());
            Product product3 = repo.save(product2);
            logger.info("Updated product " + id);
            return product3;
        } catch (Exception ex) {
            logger.error("Failed product update:", ex);
            throw new ServiceException("Failed product update:" + ex);
        }
    }

    // delete product with given id
    public void deleteProduct(Long id) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found " + id.toString()));
        try {
            repo.delete(product);
            logger.info("Deleted product " + id);
        } catch (Exception ex) {
            logger.error("Failed product delete:", ex);
            throw new ServiceException("Failed product delete:" + ex);
        }
    }

    // delete all products in repo
    public void deleteAll() {
        repo.deleteAll();
        logger.info("All products deleted");
    }
}
