package com.example.fly3.repos;

import com.example.fly3.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    List<Category> findByParentCat(Long parentId);

    Category findByName(String name);
}
