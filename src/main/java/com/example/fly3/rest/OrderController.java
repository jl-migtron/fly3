package com.example.fly3.rest;

import com.example.fly3.model.Order;
import com.example.fly3.model.OrderItem;
import com.example.fly3.model.Payment;
import com.example.fly3.services.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<Order> createOrder(@RequestParam Integer seatNum, @RequestParam String seatLetter) {
        Order order = orderService.createOrder(seatNum, seatLetter);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok().body(order);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestParam String email, @RequestBody List<OrderItem> orderItems) {
        Order order = orderService.updateOrder(id, email, orderItems);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<Order> finishOrder(@PathVariable Long id, @RequestBody Payment payment) {
        Order order = orderService.finishOrder(id, payment);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
