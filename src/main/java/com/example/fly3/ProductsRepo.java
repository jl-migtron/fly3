package com.example.fly3;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Repository
public interface ProductsRepo extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);
}
