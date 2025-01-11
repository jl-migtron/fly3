package com.example.fly3.repos;

import com.example.fly3.model.Order;
import com.example.fly3.model.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findAllByStatus(OrderStatus status);
}
