package com.example.fly3;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.model.Category;
import com.example.fly3.model.Product;
import com.example.fly3.repos.CategoryRepo;
import com.example.fly3.services.ProductService;
import java.io.File;
import java.util.List;
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
        productsService.createProduct(sodas.getId(), product);

        List<Product> products = productsService.getAllProducts();
        Product product2 = products.get(0);

        assertTrue(products.size() == 1);
        assertEquals(product.getName(), product2.getName());
        assertEquals(product.getPrice(), product2.getPrice());
        assertEquals(product.getCategory().getId(), product2.getCategory().getId());
        assertEquals(product.getImage(), product2.getImage());
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
//    @Test
//    public void testGetProductById() throws Exception {
//
//        // Create new cart & get its id
//        final Integer CART_ID = productsService.createCategory().getId();
//        // Send GET request for the new cart
//        ResultActions result = mockMvc.perform(get("/api/carts/{id}", CART_ID));
//
//        // Assert that the new cart is returned
//        result.andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.id", is(CART_ID)));
//    }
//
//    @Test
//    public void testUpdateProduct() throws Exception {
//
//        // Create new cart & get its id
//        final Integer CART_ID = productsService.createProduct().getId();
//        String addedProductsJson = "[{\"id\": 1011, \"desc\": \"martillo\", \"amount\": 1}, {\"id\": 1022, \"desc\": \"tornillos\", \"amount\": 100}]";
//
//        // Send PUT request with 2 products for the new cart
//        ResultActions result = mockMvc.perform(put("/api/carts/{id}", CART_ID)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(addedProductsJson));
//
//        // Assert that the new cart now has 2 products
//        result.andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.id", is(CART_ID)))
//            .andExpect(jsonPath("$.products", aMapWithSize(2)));
//    }
//
//    @Test
//    public void testDeleteProduct() throws Exception {
//
//        // Create new cart & get its id
//        final Integer CART_ID = productsService.createProduct().getId();
//        // Send DELETE request for the new cart
//        ResultActions result = mockMvc.perform(delete("/api/carts/{id}", CART_ID));
//
//        // Assert that no content status is returned
//        result.andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void testGetWrongProduct() throws Exception {
//
//        final Integer CART_ID = 99;
//        // Send GET request for a non existing cart
//        ResultActions result = mockMvc.perform(get("/api/carts/{id}", CART_ID));
//
//        // Assert that not found returned
//        result.andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testUpdateWrongProduct() throws Exception {
//
//        final Integer CART_ID = 99;
//        String addedProductsJson = "[{\"id\": 1011, \"desc\": \"martillo\", \"amount\": 1}, {\"id\": 1022, \"desc\": \"tornillos\", \"amount\": 100}]";
//
//        // Send PUT request with 2 products for a non existing cart
//        ResultActions result = mockMvc.perform(put("/api/carts/{id}", CART_ID)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(addedProductsJson));
//
//        // Assert that not found returned
//        result.andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testDeleteWrongProduct() throws Exception {
//
//        final Integer CART_ID = 99;
//        // Send DELETE request for a non existing cart
//        ResultActions result = mockMvc.perform(delete("/api/carts/{id}", CART_ID));
//
//        // Assert that not found returned
//        result.andExpect(status().isNotFound());
//    }
}
