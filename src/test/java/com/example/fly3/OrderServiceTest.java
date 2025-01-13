package com.example.fly3;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.model.Order;
import com.example.fly3.model.OrderItem;
import com.example.fly3.model.OrderStatus;
import com.example.fly3.model.Payment;
import com.example.fly3.model.PaymentStatus;
import com.example.fly3.services.OrderService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceTest {

    private final Long WRONG_ID = -1000L;
    private final int STOCK_SIZE = 10;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    // set categories and clean products before each test
    public void prepare() throws Exception {
        orderService.deleteAll();
    }

    @Test
    public void whenOrderCreated_thenOrderExists() throws Exception {

        Order order = orderService.createOrder(25, "D");
        List<Order> orders = orderService.getAllOrders();

        assertThat(orders, hasItem(order));
        assertEquals(OrderStatus.OPEN, orders.get(0).getStatus());
    }

    @Test
    public void givenOrderCreated_whenOrderFetchedById_thenOrderReturned() throws Exception {

        Order order = orderService.createOrder(25, "D");

        Order order2 = orderService.getOrderById(order.getId());

        assertEquals(order, order2);
    }

    @Test
    public void givenOrderCreated_whenWrongOrderFetched_thenNotFoundException() throws Exception {

        Order order = orderService.createOrder(25, "D");

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.getOrderById(WRONG_ID));
    }

    @Test
    public void givenOrderCreated_whenOrderCancelled_thenOrderDropped() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";

        Order order2 = orderService.cancelOrder(order.getId());

        assertEquals(OrderStatus.DROPPED, order2.getStatus());
    }

    @Test
    public void givenOrderUpdated_whenOrderCancelled_thenStockRestored() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";
        orderService.initStock(STOCK_SIZE);
        List<OrderItem> items = createTestOrderItems(5);
        orderService.updateOrder(order.getId(), email, items);

        Order order2 = orderService.cancelOrder(order.getId());

        assertEquals(STOCK_SIZE, orderService.getProductStock(items.get(0).getProductId()));
    }

    @Test
    public void givenOrderCreated_whenOrderUpdated_thenChangesApplied() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";

        orderService.initStock(STOCK_SIZE);
        List<OrderItem> items = createTestOrderItems(5);
        Order order2 = orderService.updateOrder(order.getId(), email, items);

        List<OrderItem> items2 = order2.getItems();
        for (int i = 0; i < items.size(); i++) {
            assertEquals(items.get(i), items2.get(i));
        }
        assertEquals(email, order2.getBuyer().getEmail());
    }

    @Test
    public void givenOrderCreated_whenOrderUpdatedwithExcess_thenChangesApplied() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";

        orderService.initStock(STOCK_SIZE);
        List<OrderItem> items = createTestOrderItems(STOCK_SIZE + 5);
        Order order2 = orderService.updateOrder(order.getId(), email, items);

        List<OrderItem> items2 = order2.getItems();
        for (int i = 0; i < items.size(); i++) {
            assertEquals(items.get(i), items2.get(i));
        }
        assertEquals(email, order2.getBuyer().getEmail());
        assertEquals(STOCK_SIZE, items.get(0).getQuantity());
    }

    @Test
    public void givenOrderCreatedAndUpdated_whenPaidOK_thenOrderFinished() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";

        List<OrderItem> items = createTestOrderItems(5);
        orderService.updateOrder(order.getId(), email, items);
        Order order2 = orderService.finishOrder(order.getId(), createTestPayment(PaymentStatus.PAID));

        assertEquals(OrderStatus.FINISHED, order2.getStatus());
    }

    @Test
    public void givenOrderCreatedAndUpdated_whenPaidOffline_thenOrderFinished() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";

        List<OrderItem> items = createTestOrderItems(5);
        orderService.updateOrder(order.getId(), email, items);
        Order order2 = orderService.finishOrder(order.getId(), createTestPayment(PaymentStatus.OFFLINEPAYMENT));

        assertEquals(OrderStatus.FINISHED, order2.getStatus());
    }

    @Test
    public void givenOrderCreatedAndUpdated_whenPaymentFailed_thenOrderCancelled() throws Exception {

        Order order = orderService.createOrder(25, "D");
        String email = "mark@gmail.com";

        orderService.initStock(STOCK_SIZE);
        List<OrderItem> items = createTestOrderItems(5);
        orderService.updateOrder(order.getId(), email, items);
        Order order2 = orderService.finishOrder(order.getId(), createTestPayment(PaymentStatus.PAYMENTFAILED));

        assertEquals(OrderStatus.DROPPED, order2.getStatus());
        assertEquals(STOCK_SIZE, orderService.getProductStock(items.get(0).getProductId()));
    }

    @Test
    public void givenOrderCreated_whenOrderDeleted_thenRepoEmpty() throws Exception {

        Order order = orderService.createOrder(25, "D");

        orderService.deleteOrder(order.getId());

        assertTrue(orderService.getAllOrders().isEmpty());
    }

    private List<OrderItem> createTestOrderItems(int quantity) {
        List<OrderItem> items = new ArrayList<>();
        Long prod1Id = 13L;
        Long prod2Id = 14L;
        items.add(new OrderItem(null, prod1Id, quantity, 100, null));
        items.add(new OrderItem(null, prod2Id, quantity, 100, null));

        return items;
    }

    private Payment createTestPayment(PaymentStatus status) {
        return new Payment("K998877", status, new Date(), "VISA");
    }
}
