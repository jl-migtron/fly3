package com.example.fly3.services;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Component
@Data
// Simple stock simulator
public class SimpleStock {

    Logger logger = LoggerFactory.getLogger(SimpleStock.class);
    public static final int INITIAL_FILL = 10;
    private Map<Long, Integer> stock = new HashMap<>();

    public SimpleStock() {
        initStock(INITIAL_FILL);
    }

    public int getProductStock(long prodId) {
        try {
            return stock.get(prodId);
        } catch (Exception ex) {
            logger.error("Not stocked product ", ex);
            return 0;
        }
    }

    // initialitzes all products stocks to the given value 
    public void initStock(int amount) {
        for (long i = 1L; i < 20L; i++) {
            stock.put(i, amount);
        }
    }

    // refills an amount of product stock
    public void refillStock(long prodId, int amount) {
        changeProductStock(prodId, amount);
    }

    // consumes an amount of product stock
    // returns the consumed amount (positive)
    public int consumeStock(long prodId, int amount) {
        return -changeProductStock(prodId, -amount);
    }

    // changes a product stock by a given amount limiting the applied change if zero stock reached
    // the final applied change is always returned
    private int changeProductStock(long prodId, int change) {
        try {
            // if product exists, fetch amount
            int available = (stock.containsKey(prodId)) ? stock.get(prodId) : 0;
            int finalAmount = available + change;
            if (finalAmount >= 0) {
                stock.put(prodId, finalAmount);
                return change;
            } else {
                // if not enough stock, just consume the available amount
                stock.put(prodId, 0);
                return -available;
            }
        } catch (Exception ex) {
            logger.error("Error changing stock ", ex);
            return 0;
        }
    }

}
