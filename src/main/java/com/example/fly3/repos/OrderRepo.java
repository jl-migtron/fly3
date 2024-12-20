package com.example.fly3.repos;

import com.example.fly3.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

}
