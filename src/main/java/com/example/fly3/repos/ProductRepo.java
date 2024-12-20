package com.example.fly3.repos;

import com.example.fly3.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findAllByCategory_Id(Long categorId);
}
