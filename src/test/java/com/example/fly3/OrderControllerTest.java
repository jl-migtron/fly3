package com.example.fly3;

import com.example.fly3.model.Buyer;
import com.example.fly3.model.Order;
import com.example.fly3.model.OrderItem;
import com.example.fly3.model.OrderStatus;
import com.example.fly3.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
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

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    public static final String TEST_ORDERS_URL = "/api/orders";
    public static final String TEST_ORDERS_URL2 = "/api/orders/{id}";
    public static final String TEST_ORDER_UPDATE_URL = "/api/orders/{id}/update";
    public static final String TEST_ORDER_FINISH_URL = "/api/orders/{id}/finish";

    @Test
    public void testCreateOrder() throws Exception {
        Order order = createTestOpenOrder();
        when(service.createOrder(anyInt(), anyString())).thenReturn(order);

        //String jsonRequestBody = new ObjectMapper().writeValueAsString(order);
        // Send POST request with order        
        ResultActions result = mockMvc.perform(post(TEST_ORDERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .param("seatNum", Integer.toString(SEAT))
            .param("seatLetter", LETTER));

        // Assert that order is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.buyer.seatNum").value(SEAT))
            .andExpect(jsonPath("$.buyer.seatLetter").value(LETTER));
    }

    @Test
    public void testGetOrderById() throws Exception {

        Order order = createTestOpenOrder();
        when(service.createOrder(anyInt(), anyString())).thenReturn(order);

        // Send GET request for order
        ResultActions result = mockMvc.perform(get(TEST_ORDERS_URL2, ID));

        // Assert that order is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID));
    }

    @Test
    public void testUpdateOrder() throws Exception {

        Order order = createTestOpenOrder();
        order.setItems(createTestOrderItems());
        Long prodId = order.getId();
        when(service.updateOrder(anyLong(), anyString(), anyList())).thenReturn(order);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = "{";
        for (OrderItem item : order.getItems()) {
            jsonRequestBody += mapper.writeValueAsString(item) + ",";
        }
        jsonRequestBody += "}";

        // Send POST request with order
        ResultActions result = mockMvc.perform(put(TEST_ORDER_UPDATE_URL, order.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));

        // Assert that order is returned
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem orderItem = order.getItems().get(i);
            result.andExpect(jsonPath("$[" + i + "].productId").value(orderItem.getProductId()))
                .andExpect(jsonPath("$[" + i + "].quantity").value(orderItem.getQuantity()))
                .andExpect(jsonPath("$[" + i + "].price").value(orderItem.getPrice()));
        }
    }

    @Test
    public void testDeleteOrder() throws Exception {

        doNothing().when(service).deleteOrder(anyLong());

        // Send DELETE request with order
        ResultActions result = mockMvc.perform(delete(TEST_ORDERS_URL, 123L));

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

    private List<OrderItem> createTestOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        Long prod1Id = 123L;
        Long prod2Id = 124L;
        items.add(new OrderItem(null, prod1Id, 4, 100, null));
        items.add(new OrderItem(null, prod2Id, 4, 100, null));

        return items;
    }
}
