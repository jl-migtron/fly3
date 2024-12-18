package com.example.fly3;

import java.io.File;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductsRepoTest {

    @Autowired
    ProductsRepo productsRepo;

    @BeforeEach
    // clean products repo before each test
    public void prepare() throws Exception {
        productsRepo.deleteAll();
    }

    @Test
    public void whenProductCreated_thenProductFetched() throws Exception {

        Category category = new Category("drinks", null);
        Product product = new Product("coke", 100, category, new File("/coke.img"));
        productsRepo.save(product);

        List<Product> products = productsRepo.findAll();

        assertTrue(products.size() == 1);
        assertEquals(product, products.get(0));
    }

    @Test
    public void givenProductCreated_whenProductFetchedById_thenProductReturned() throws Exception {

        Category category = new Category("drinks", null);
        Product product = new Product("coke", 100, category, new File("/coke.img"));
        Product product2 = productsRepo.save(product);

        Product product3 = productsRepo.findById(product2.getId()).get();

        assertEquals(product2, product3);
    }

    @Test
    public void givenProductCreated_whenWrongProductFetched_thenNoProductReturned() throws Exception {

        Category category = new Category("drinks", null);
        Product product = new Product("coke", 100, category, new File("/coke.img"));
        productsRepo.save(product);

        final Long WRONG_ID = -1000L;
        Optional<Product> product2 = productsRepo.findById(WRONG_ID);

        assertFalse(product2.isPresent());
    }

    @Test
    public void givenProductCreated_whenProductChangedAndSaved_thenChangedProductReturned() throws Exception {

        Category category = new Category("drinks", null);
        Product product = new Product("coke", 100, category, new File("/coke.img"));
//        Product product2 = new Product("fanta", 111, category, new File("/fanta.img"));
        Product product2 = productsRepo.save(product);

        product2.setName("fanta");
        product2.setPrice(111);
        product2.setImage(new File("/fanta.img"));
        Product product3 = productsRepo.save(product2);

        assertEquals(product2.getName(), product3.getName());
        assertEquals(product2.getPrice(), product3.getPrice());
        assertEquals(product2.getImage(), product3.getImage());
    }

    @Test
    public void givenProductCreated_whenProductDeleted_thenRepoEmpty() throws Exception {

        Category category = new Category("drinks", null);
        Product product = new Product("coke", 100, category, new File("/coke.img"));
        Product product2 = productsRepo.save(product);

        productsRepo.deleteById(product2.getId());
        List<Product> products = productsRepo.findAll();

        assertTrue(products.isEmpty());
    }

    @Test
    public void givenProductCreated_whenWrongProductDeleted_thenRepoNotEmpty() throws Exception {

        Category category = new Category("drinks", null);
        Product product = new Product("coke", 100, category, new File("/coke.img"));
        productsRepo.save(product);

        final Long WRONG_ID = -1000L;
        productsRepo.deleteById(WRONG_ID);
        List<Product> products = productsRepo.findAll();

        assertFalse(products.isEmpty());
    }
}
