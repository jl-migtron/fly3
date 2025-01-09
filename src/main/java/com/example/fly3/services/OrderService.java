package com.example.fly3.services;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.exceptions.ServiceException;
import com.example.fly3.model.Buyer;
import com.example.fly3.model.Order;
import com.example.fly3.model.OrderItem;
import com.example.fly3.model.OrderStatus;
import com.example.fly3.model.Payment;
import com.example.fly3.model.PaymentStatus;
import com.example.fly3.repos.OrderItemRepo;
import com.example.fly3.repos.OrderRepo;
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
public class OrderService {

    Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderRepo repo;
    @Autowired
    OrderItemRepo itemRepo;

    public Order createOrder(int seatNum, String seatLetter) {
        try {
            Order order = new Order();
            order.setBuyer(new Buyer(null, null, seatNum, seatLetter, order));
            order.setStatus(OrderStatus.OPEN);
            order.setPrice(0);
            Order order2 = repo.save(order);
            logger.info("Created order " + order2);
            return order2;
        } catch (Exception ex) {
            throw new ServiceException("Failed order creation:" + ex);
        }
    }

    // get all orders in repo
    public List<Order> getAllOrders() {
        return repo.findAll();
    }

    // get order with given id
    public Order getOrderById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
    }

    // delete order with given id
    public void deleteOrder(Long id) {
        Order cat2 = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            repo.delete(cat2);
            logger.info("Deleted order " + id);
        } catch (Exception ex) {
            logger.error("Failed order delete:", ex);
            throw new ServiceException("Failed order delete:" + ex);
        }
    }

    // delete all orders in repo
    public void deleteAll() {
        repo.deleteAll();
        logger.info("All orders deleted");
    }

    // update order with given id
    public Order updateOrder(Long id, String email, List<OrderItem> orderItems) {
        Order order2 = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            order2.getBuyer().setEmail(email);
            checkStock(orderItems);
            int price = computePrice(orderItems);
            order2.setPrice(price);
            repo.save(order2);
            orderItems.forEach(item -> {
                item.setOrder(order2);
                itemRepo.save(item);
            });
            logger.info("Updated order " + id);
            return order2;
        } catch (Exception ex) {
            logger.error("Failed order update:", ex);
            throw new ServiceException("Failed order update:" + ex);
        }
    }

    // update order with given id
    public Order finishOrder(Long id, Payment payment) {
        Order order2 = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            order2.setPayment(payment);
            if (payment.getStatus().equals(PaymentStatus.PAID)) {
                updateStock(order2.getItems());
            }
            repo.save(order2);
            logger.info("Finished order " + id);
            return order2;
        } catch (Exception ex) {
            logger.error("Failed order finish:", ex);
            throw new ServiceException("Failed order finish:" + ex);
        }
    }

    private void checkStock(List<OrderItem> items) {

    }

    private int computePrice(List<OrderItem> items) {
        int price = items.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
        return price;
    }

    private void updateStock(List<OrderItem> items) {
        // TO DO
    }
}
