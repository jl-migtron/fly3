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
    CategoriesRepo categoriesRepo;

    // get all categories in repo
    public List<Category> getAllCategories() {
        return categoriesRepo.findAll();
    }

    // get category with given id
    public Category getCategoryById(Long id) {
        return categoriesRepo.findById(id).orElseThrow();
    }

    // get category with given id
    public List<Category> getSubcategoriesForId(Long id) {
        return categoriesRepo.findByParentCat(id);
    }

    // create a category
    public Category createCategory(Category category) {
        try {
            Category category2 = categoriesRepo.save(category);
            logger.info("Created category " + category2.getId().toString());
            return category2;
        } catch (Exception ex) {
            logger.error("Failed category creation", ex);
            return null;
        }
    }

    // update category with given id
    public Category updateCategory(Long id, Category category) {
        try {
            Optional<Category> category2 = categoriesRepo.findById(id);

            category2.ifPresentOrElse(cat2 -> {
                cat2.setName(category.getName());
                cat2.setParentCat(category.getParentCat());
                cat2.setListProducts(category.getListProducts());
                categoriesRepo.save(cat2);
                logger.info("Updated category " + id);
            },
                () -> logger.warn("Failed category update: no category found with id " + id));

            return category2.get();
        } catch (Exception ex) {
            logger.error("Failed category update", ex);
            return null;
        }
    }

    // add products to category with given id
    public Category addCategoryProducts(Long id, List<Product> products) {
        try {
            Optional<Category> category2 = categoriesRepo.findById(id);

            category2.ifPresentOrElse(cat2 -> {
                cat2.getListProducts().addAll(products);
                categoriesRepo.save(cat2);
                logger.info("Added " + products.size() + " products to category " + id);
            },
                () -> logger.warn("Failed adding category products: no category found with id " + id));

            return category2.get();
        } catch (Exception ex) {
            logger.error("Failed adding category products", ex);
            return null;
        }
    }

    // add products to category with given id
    public Category deleteCategoryProducts(Long id, List<Product> products) {
        try {
            Optional<Category> category2 = categoriesRepo.findById(id);

            category2.ifPresentOrElse(cat2 -> {
                cat2.getListProducts().removeAll(products);
                categoriesRepo.save(cat2);
                logger.info("Deleted " + products.size() + " products from category " + id);
            },
                () -> logger.warn("Failed deleting category products: no category found with id " + id));

            return category2.get();
        } catch (Exception ex) {
            logger.error("Failed deleting category products", ex);
            return null;
        }
    }

    // delete category with given id
    public boolean deleteCategory(Long id) {
        try {
            Optional<Category> category2 = categoriesRepo.findById(id);

            category2.ifPresentOrElse(cat2 -> {
                categoriesRepo.deleteById(id);
                logger.info("Deleted category " + id);
            },
                () -> logger.warn("Failed category delete: no category found with id " + id));

            return category2.isPresent();
        } catch (Exception ex) {
            logger.error("Failed category delete", ex);
            return false;
        }
    }

    // delete all categories in repo
    public void deleteAll() {
        categoriesRepo.deleteAll();
        logger.info("All categories deleted");
    }
}
