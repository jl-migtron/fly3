package com.example.fly3.services;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.exceptions.ServiceException;
import com.example.fly3.model.Buyer;
import com.example.fly3.model.Order;
import com.example.fly3.model.OrderItem;
import com.example.fly3.model.OrderStatus;
import com.example.fly3.model.Payment;
import static com.example.fly3.model.PaymentStatus.OFFLINEPAYMENT;
import static com.example.fly3.model.PaymentStatus.PAID;
import static com.example.fly3.model.PaymentStatus.PAYMENTFAILED;
import com.example.fly3.repos.OrderItemRepo;
import com.example.fly3.repos.OrderRepo;
import jakarta.transaction.Transactional;
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
    OrderRepo ordersRepo;
    @Autowired
    OrderItemRepo itemsRepo;
    @Autowired
    SimpleStock stock;

    public Order createOrder(int seatNum, String seatLetter) {
        try {
            Order order = new Order();
            order.setBuyer(new Buyer(null, seatNum, seatLetter));
            order.setStatus(OrderStatus.OPEN);
            order.setPrice(0);
            Order order2 = ordersRepo.save(order);
            logger.info("Created order " + order2);
            return order2;
        } catch (Exception ex) {
            throw new ServiceException("Failed order creation:" + ex);
        }
    }

    // cancel order with given id
    public Order cancelOrder(Long id) {
        Order order = ordersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            order.setStatus(OrderStatus.DROPPED);
            Order order2 = ordersRepo.save(order);
            logger.info("Cancelled order " + order2);
            return order2;
        } catch (Exception ex) {
            logger.error("Failed order cancel:", ex);
            throw new ServiceException("Failed order cancel:" + ex);
        }
    }

    // get all orders in repo
    public List<Order> getAllOrders() {
        return ordersRepo.findAll();
    }

    // get order with given id
    public Order getOrderById(Long id) {
        return ordersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
    }

    // get orders by status
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return ordersRepo.findAllByStatus(status);
    }

    // delete order with given id
    public void deleteOrder(Long id) {
        Order cat2 = ordersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            ordersRepo.delete(cat2);
            logger.info("Deleted order " + id);
        } catch (Exception ex) {
            logger.error("Failed order delete:", ex);
            throw new ServiceException("Failed order delete:" + ex);
        }
    }

    // delete all orders in repo
    public void deleteAll() {
        ordersRepo.deleteAll();
        logger.info("All orders deleted");
    }

    // update order with given id
    @Transactional
    public Order updateOrder(Long id, String email, List<OrderItem> orderItems) {
        Order order = ordersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            order.getBuyer().setEmail(email);
            logger.debug("stock: " + stock.toString());
            logger.debug("pre consume: " + orderItems);
            consumeStock(orderItems);
            logger.debug("post consume: " + orderItems);
            int price = computePrice(orderItems);
            order.setPrice(price);
            order.setItems(orderItems);
            ordersRepo.save(order);
            orderItems.forEach(item -> {
                item.setOrder(order);
                itemsRepo.save(item);
            });
            logger.info("Updated order " + id);
            return order;
        } catch (Exception ex) {
            logger.error("Failed order update:", ex);
            throw new ServiceException("Failed order update:" + ex);
        }
    }

    // finish order with given id
    @Transactional
    public Order finishOrder(Long id, Payment payment) {
        Order order = ordersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found " + id.toString()));
        try {
            order.setPayment(payment);
            switch (payment.getPayStatus()) {
                case PAID, OFFLINEPAYMENT -> {
                    order.setStatus(OrderStatus.FINISHED);
                }
                case PAYMENTFAILED -> {
                    order.setStatus(OrderStatus.DROPPED);
                    restoreStock(order.getItems());
                    logger.warn("Failed payment -> order dropped " + id);
                }
            }
            ordersRepo.save(order);
            logger.info("Finished order " + id);
            return order;
        } catch (Exception ex) {
            logger.error("Failed order finish:", ex);
            throw new ServiceException("Failed order finish:" + ex);
        }
    }

    // initialized the stock of all products to given amount
    public void initStock(int amount) {
        stock.initStock(amount);
    }

    // order items are checked against the available stock
    // and consumed accordingly
    private void consumeStock(List<OrderItem> items) {
        items.forEach(item -> {
            int consumed = stock.consumeStock(item.getProductId(), item.getQuantity());
            if (consumed != item.getQuantity()) {
                item.setQuantity(consumed);
            }
        });
    }

    // stock is refilled with previously consumed items (due to failed payment for order)
    private void restoreStock(List<OrderItem> items) {
        items.forEach(item -> stock.refillStock(item.getProductId(), item.getQuantity()));
    }

    private int computePrice(List<OrderItem> items) {
        int price = items.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
        return price;
    }
}
