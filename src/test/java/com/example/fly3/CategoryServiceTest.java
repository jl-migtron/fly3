package com.example.fly3;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.model.Category;
import com.example.fly3.services.CategoryService;
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
class CategoryServiceTest {

    private final String FOOD = "food";
    private final String DRINKS = "drinks";
    private final String SNACKS = "snacks";
    private final String SODAS = "sodas";
    private final Long WRONG_ID = -1000L;

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    // clean stored categories before each test
    public void prepare() throws Exception {
        // categories        
        categoryService.deleteAll();
    }

    @Test
    public void whenCategoryCreated_thenCategoryExists() throws Exception {

        Category food = new Category(null, FOOD, null, null);
        food = categoryService.createCategory(food);

        List<Category> cats = categoryService.getAllCategories();

        assertThat(cats, hasItem(food));
    }

    @Test
    public void givenCategoryCreated_whenCategoryFetchedById_thenCategoryReturned() throws Exception {

        Category food = new Category(null, FOOD, null, null);
        food = categoryService.createCategory(food);

        Category product = categoryService.getCategoryById(food.getId());

        assertEquals(food, product);
    }

    @Test
    public void givenCategoryCreated_whenWrongCategoryFetched_thenNotFoundException() throws Exception {

        Category food = new Category(null, FOOD, null, null);
        food = categoryService.createCategory(food);

        assertThrows(ResourceNotFoundException.class,
            () -> categoryService.getCategoryById(WRONG_ID));
    }

    @Test
    public void givenSubcategoriesCreated_whenParentSubcatsFetched_thenSubcategoriesReturned() throws Exception {

        Category food = new Category(null, FOOD, null, null);
        Category drinks = new Category(null, DRINKS, null, null);
        food = categoryService.createCategory(food);
        drinks = categoryService.createCategory(drinks);
        Category snacks = new Category(null, SNACKS, food.getId(), null);
        categoryService.createCategory(snacks);

        List<Category> subcats = categoryService.getSubcategoriesForId(food.getId());

        assertThat(subcats, hasItem(snacks));
    }

    @Test
    public void givenCategoryCreated_whenCategoryChangedAndSaved_thenChangedCategoryReturned() throws Exception {

        Category cat1 = new Category(null, FOOD, null, null);
        cat1 = categoryService.createCategory(cat1);

        cat1.setName("fanta");
        cat1.setParentCat(50L);
        Category cat2 = categoryService.updateCategory(cat1.getId(), cat1);

        assertEquals(cat1, cat2);
    }

    @Test
    public void givenCategoryCreated_whenCategoryDeleted_thenRepoEmpty() throws Exception {

        Category food = new Category(null, FOOD, null, null);
        food = categoryService.createCategory(food);

        categoryService.deleteCategory(food.getId());

        assertTrue(categoryService.getAllCategories().isEmpty());
    }

    @Test
    public void givenCategoryCreated_whenWrongCategoryDeleted_thenNotFoundException() throws Exception {

        Category food = new Category(null, FOOD, null, null);
        food = categoryService.createCategory(food);

        assertThrows(ResourceNotFoundException.class,
            () -> categoryService.deleteCategory(WRONG_ID));
    }
}
