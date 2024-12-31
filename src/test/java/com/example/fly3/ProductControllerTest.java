package com.example.fly3;

import com.example.fly3.model.Category;
import com.example.fly3.model.Product;
import com.example.fly3.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    private final String DRINKS = "drinks";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    public static final String TEST_CAT_PROD_URL = "/api/categories/{catId}/products";
    public static final String TEST_PROD_URL = "/api/categories/products/{id}";

    @Test
    public void testCreateProduct() throws Exception {
        Product coke = createTestProduct();
        Long catId = coke.getCategory().getId();
        Long prodId = coke.getId();
        when(service.createProduct(anyLong(), any(Product.class))).thenReturn(coke);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(coke);

        // Send POST request with product
        ResultActions result = mockMvc.perform(post(TEST_CAT_PROD_URL, catId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that product is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(prodId));
    }

    @Test
    public void testGetProductById() throws Exception {

        Product coke = createTestProduct();
        Long prodId = coke.getId();
        when(service.getProductById(anyLong())).thenReturn(coke);

        // Send GET request for product
        ResultActions result = mockMvc.perform(get(TEST_PROD_URL, prodId));

        // Assert that product is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(prodId));
    }

    @Test
    public void testGetProductsByCategory() throws Exception {

        List<Product> products = createTestProducts();
        Long catId = products.get(0).getCategory().getId();
        when(service.getProductsByCategory(anyLong())).thenReturn(products);

        // Send GET request for category products
        ResultActions result = mockMvc.perform(get(TEST_CAT_PROD_URL, catId));

        // Assert that products are returned       
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            result.andExpect(jsonPath("$[" + i + "].id").value(product.getId()))
                .andExpect(jsonPath("$[" + i + "].name").value(product.getName()));
        }
    }

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
    private Product createTestProduct() {
        Long catId = 25L;
        Long prodId = 123L;
        Category drinks = new Category(catId, DRINKS, null, null);
        return new Product(prodId, "coke", 100, drinks, new File("/coke.img"));
    }

    private List<Product> createTestProducts() {
        List<Product> products = new ArrayList<>();
        Long catId = 25L;
        Long prod1Id = 123L;
        Long prod2Id = 124L;
        Category drinks = new Category(catId, DRINKS, null, null);
        products.add(new Product(prod1Id, "coke", 100, drinks, new File("/coke.img")));
        products.add(new Product(prod2Id, "fanta", 100, drinks, new File("/fanta.img")));

        return products;
    }
}
