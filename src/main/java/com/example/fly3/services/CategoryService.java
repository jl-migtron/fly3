package com.example.fly3.services;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.exceptions.ServiceException;
import com.example.fly3.model.Category;
import com.example.fly3.repos.CategoryRepo;
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
public class CategoryService {

    Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    CategoryRepo repo;

    public Category createCategory(Category category) {
        try {
            Category category2 = repo.save(category);
            logger.info("Created category " + category2);
            return category2;
        } catch (Exception ex) {
            throw new ServiceException("Failed category creation:" + ex);
        }
    }

    // get all categories in repo
    public List<Category> getAllCategories() {
        return repo.findAll();
    }

    // get category with given id
    public Category getCategoryById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found " + id.toString()));
    }

    // get categoris that have parent category with given id
    public List<Category> getSubcategoriesForId(Long id) {
        return repo.findByParentCat(id);
    }

    // update category with given id
    public Category updateCategory(Long id, Category category) {
        try {
            Category cat2 = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found " + id.toString()));

            cat2.setName(category.getName());
            cat2.setParentCat(category.getParentCat());
            repo.save(cat2);
            logger.info("Updated category " + id);
            return cat2;
        } catch (Exception ex) {
            throw new ServiceException("Failed category update:" + ex);
        }
    }

    // delete category with given id
    public void deleteCategory(Long id) {
        try {
            Category cat2 = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found " + id.toString()));

            repo.delete(cat2);
            logger.info("Deleted category " + id);
        } catch (Exception ex) {
            throw new ServiceException("Failed category delete:" + ex);
        }
    }

    // delete all categories in repo
    public void deleteAll() {
        repo.deleteAll();
        logger.info("All categories deleted");
    }
}
