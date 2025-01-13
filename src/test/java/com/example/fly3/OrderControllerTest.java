package com.example.fly3;

import com.example.fly3.exceptions.ResourceNotFoundException;
import com.example.fly3.model.Buyer;
import com.example.fly3.model.Order;
import com.example.fly3.model.OrderItem;
import com.example.fly3.model.OrderStatus;
import com.example.fly3.model.Payment;
import com.example.fly3.model.PaymentStatus;
import static com.example.fly3.model.PaymentStatus.OFFLINEPAYMENT;
import static com.example.fly3.model.PaymentStatus.PAID;
import static com.example.fly3.model.PaymentStatus.PAYMENTFAILED;
import com.example.fly3.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    private final Long ID = 1L;
    private final int SEAT = 25;
    private final String LETTER = "D";
    private final Long WRONG_ID = -1000L;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    public static final String TEST_ORDERS_URL = "/api/orders";
    public static final String TEST_ORDERS_URL2 = "/api/orders/{id}";
    public static final String TEST_ORDERS_CANCEL_URL = "/api/orders/{id}/cancel";
    public static final String TEST_ORDERS_UPDATE_URL = "/api/orders/{id}/update";
    public static final String TEST_ORDERS_FINISH_URL = "/api/orders/{id}/finish";

    @Test
    public void testCreateOrder() throws Exception {
        Order order = createTestOpenOrder();
        when(service.createOrder(anyInt(), anyString())).thenReturn(order);

        // Send POST request with order        
        ResultActions result = mockMvc.perform(post(TEST_ORDERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .param("seatnum", Integer.toString(SEAT))
            .param("seatletter", LETTER));

        // Assert that order is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.buyer.seatNum").value(SEAT))
            .andExpect(jsonPath("$.buyer.seatLetter").value(LETTER));
    }

    @Test
    public void testGetOrderById() throws Exception {

        Order order = createTestOpenOrder();
        when(service.getOrderById(anyLong())).thenReturn(order);

        // Send GET request for order
        ResultActions result = mockMvc.perform(get(TEST_ORDERS_URL2, ID));

        // Assert that order is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID));
    }

    @Test
    public void testGetOrdersByStatus() throws Exception {

        List<Order> orders = Arrays.asList(createTestOpenOrder(), createTestOpenOrder());
        when(service.getOrdersByStatus(any(OrderStatus.class))).thenReturn(orders);

        // Send GET request for order
        ResultActions result = mockMvc.perform(get(TEST_ORDERS_URL + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .param("status", OrderStatus.OPEN.name()));

        // Assert that order is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            result.andExpect(jsonPath("$[" + i + "].status").value(OrderStatus.OPEN.name()));
        }
    }

    @Test
    public void testCancelOrder() throws Exception {

        Order order = createTestOpenOrder();
        order.setStatus(OrderStatus.DROPPED);
        when(service.cancelOrder(anyLong())).thenReturn(order);

        // Send PUT request with cancel
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_CANCEL_URL, order.getId()));

        // Assert that order is returned with finished status
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.DROPPED.name()));
    }

    @Test
    public void testUpdateOrder() throws Exception {

        Order order = createTestUpdatedOrder();
        when(service.updateOrder(anyLong(), anyString(), anyList())).thenReturn(order);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(order.getItems());

        // Send PUT request with order items
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_UPDATE_URL, order.getId())
            .param("email", order.getBuyer().getEmail())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that order is returned with proper items
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.buyer.email").value(order.getBuyer().getEmail()));
        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem orderItem = order.getItems().get(i);
            result.andExpect(jsonPath("$.items[" + i + "].productId").value(orderItem.getProductId()))
                .andExpect(jsonPath("$.items[" + i + "].quantity").value(orderItem.getQuantity()))
                .andExpect(jsonPath("$.items[" + i + "].price").value(orderItem.getPrice()));
        }
    }

    @Test
    public void testFinishOrderPaid() throws Exception {

        Payment payment = createTestPayment(PaymentStatus.PAID);
        Order order = createTestFinishedOrder(payment);
        when(service.finishOrder(anyLong(), any(Payment.class))).thenReturn(order);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(payment);

        // Send PUT request with payment ok
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_FINISH_URL, order.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that order is returned with finished status
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.FINISHED.name()));
    }

    @Test
    public void testFinishOrderPaidOffline() throws Exception {

        Payment payment = createTestPayment(PaymentStatus.OFFLINEPAYMENT);
        Order order = createTestFinishedOrder(payment);
        when(service.finishOrder(anyLong(), any(Payment.class))).thenReturn(order);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(payment);

        // Send PUT request with payment offline
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_FINISH_URL, order.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that order is returned with finished status
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.FINISHED.name()));
    }

    @Test
    public void testFinishOrderPaymentFailed() throws Exception {

        Payment payment = createTestPayment(PaymentStatus.PAYMENTFAILED);
        Order order = createTestFinishedOrder(payment);
        when(service.finishOrder(anyLong(), any(Payment.class))).thenReturn(order);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(payment);

        // Send PUT request with payment failed
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_FINISH_URL, order.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that order is returned with dropped status
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.DROPPED.name()));
    }

    @Test
    public void testUpdateWrongOrder() throws Exception {

        Order order = createTestUpdatedOrder();
        when(service.updateOrder(anyLong(), anyString(), anyList())).thenThrow(ResourceNotFoundException.class);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(order.getItems());

        // Send PUT request with order items
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_UPDATE_URL, WRONG_ID)
            .param("email", order.getBuyer().getEmail())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that not found returned
        result.andExpect(status().isNotFound());
    }

    @Test
    public void testFinishWrongOrder() throws Exception {

        Payment payment = createTestPayment(PaymentStatus.PAID);
        Order order = createTestFinishedOrder(payment);
        when(service.finishOrder(anyLong(), any(Payment.class))).thenThrow(ResourceNotFoundException.class);

        String jsonRequestBody = new ObjectMapper().writeValueAsString(payment);

        // Send PUT request with payment ok
        ResultActions result = mockMvc.perform(put(TEST_ORDERS_FINISH_URL, WRONG_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that not found returned
        result.andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOrder() throws Exception {

        doNothing().when(service).deleteOrder(anyLong());

        // Send DELETE request with order
        ResultActions result = mockMvc.perform(delete(TEST_ORDERS_URL2, 123L));

        // Assert that no content status is returned
        result.andExpect(status().isNoContent());
    }

    private Order createTestOpenOrder() {
        Order order = new Order();
        order.setId(ID);
        order.setBuyer(new Buyer(null, SEAT, LETTER));
        order.setStatus(OrderStatus.OPEN);
        order.setPrice(0);
        return order;
    }

    private Order createTestUpdatedOrder() {
        Order order = createTestOpenOrder();
        List<OrderItem> items = new ArrayList<>();
        Long prod1Id = 123L;
        Long prod2Id = 124L;
        items.add(new OrderItem(null, prod1Id, 4, 100, null));
        items.add(new OrderItem(null, prod2Id, 4, 100, null));
        order.setItems(items);
        order.getBuyer().setEmail("mark@gmail.com");
        order.setPrice(800);
        return order;
    }

    private Order createTestFinishedOrder(Payment payment) {
        Order order = createTestUpdatedOrder();
        switch (payment.getPayStatus()) {
            case PAID, OFFLINEPAYMENT -> {
                order.setStatus(OrderStatus.FINISHED);
            }
            case PAYMENTFAILED -> {
                order.setStatus(OrderStatus.DROPPED);
            }
        }
        return order;
    }

    private Payment createTestPayment(PaymentStatus status) {
        return new Payment("K998877", status, new Date(), "VISA");
    }
}
