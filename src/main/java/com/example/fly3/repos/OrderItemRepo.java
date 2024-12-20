package com.example.fly3.repos;

import com.example.fly3.model.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrder_Id(Long orderId);
}
