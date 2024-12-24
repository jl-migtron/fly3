package com.example.fly3;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.model.Category;
import com.example.fly3.model.Product;
import com.example.fly3.repos.CategoryRepo;
import com.example.fly3.services.ProductService;
import java.io.File;
import java.util.List;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductsServiceTest {

    private final String FOOD = "food";
    private final String DRINKS = "drinks";
    private final String SNACKS = "snacks";
    private final String SODAS = "sodas";
    private final Long WRONG_ID = -1000L;

    @Autowired
    private ProductService productsService;
    @Autowired
    private CategoryRepo catRepo;

    @BeforeEach
    // set categories and clean products before each test
    public void prepare() throws Exception {
        // categories        
        catRepo.deleteAll();
        Category food = catRepo.save(new Category(null, FOOD, null, null));
        Category snacks = catRepo.save(new Category(null, SNACKS, food.getId(), null));
        Category drinks = catRepo.save(new Category(null, DRINKS, null, null));
        Category sodas = catRepo.save(new Category(null, SODAS, drinks.getId(), null));
        productsService.deleteAll();
    }

    @Test
    public void whenProductCreated_thenProductExists() throws Exception {

        Category sodas = catRepo.findByName(SODAS);
        Product product = new Product(null, "coke", 100, null, new File("/coke.img"));
        product = productsService.createProduct(sodas.getId(), product);

        List<Product> products = productsService.getAllProducts();

        assertThat(products, hasItem(product));
    }

    @Test
    public void givenProductCreated_whenProductFetchedById_thenProductReturned() throws Exception {

        Category sodas = catRepo.findByName(SODAS);
        Product product = new Product(null, "coke", 100, null, new File("/coke.img"));
        product = productsService.createProduct(sodas.getId(), product);

        Product product2 = productsService.getProductById(product.getId());

        assertEquals(product, product2);
    }

    @Test
    public void givenProductCreated_whenWrongProductFetched_thenNotFoundException() throws Exception {

        Category sodas = catRepo.findByName(SODAS);
        Product product = new Product(null, "coke", 100, null, new File("/coke.img"));
        productsService.createProduct(sodas.getId(), product);

        assertThrows(ResourceNotFoundException.class,
            () -> productsService.getProductById(WRONG_ID));
    }

    @Test
    public void givenProductCreated_whenProductChangedAndSaved_thenChangedProductReturned() throws Exception {

        Category sodas = catRepo.findByName(SODAS);
        Product product = new Product(null, "coke", 100, null, new File("/coke.img"));
        product = productsService.createProduct(sodas.getId(), product);

        product.setName("fanta");
        product.setPrice(111);
        product.setImage(new File("/fanta.img"));

        Product product2 = productsService.updateProduct(product.getId(), product);

        assertEquals(product, product2);
    }

    @Test
    public void givenProductCreated_whenProductDeleted_thenRepoEmpty() throws Exception {

        Category sodas = catRepo.findByName(SODAS);
        Product product = new Product(null, "coke", 100, null, new File("/coke.img"));
        Product product2 = productsService.createProduct(sodas.getId(), product);

        productsService.deleteProduct(product2.getId());

        assertTrue(productsService.getAllProducts().isEmpty());
    }

    @Test
    public void givenProductCreated_whenWrongProductDeleted_thenNotFoundException() throws Exception {

        Category sodas = catRepo.findByName(SODAS);
        Product product = new Product(null, "coke", 100, null, new File("/coke.img"));
        productsService.createProduct(sodas.getId(), product);

        assertThrows(ResourceNotFoundException.class, () -> {
            productsService.deleteProduct(WRONG_ID);
        });
    }
}
