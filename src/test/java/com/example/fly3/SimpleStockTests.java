package com.example.fly3;

import com.example.fly3.services.SimpleStock;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

class SimpleStockTests {

    private static long PROD_ID = 2L;

    @Test
    void givenStockFilled_whenSomeConsumed_thenStockReduced() {
        int stockSize = 10;
        int orderSize = 5;
        SimpleStock stock = new SimpleStock();
        stock.initStock(stockSize);

        int q = stock.consumeStock(PROD_ID, orderSize);

        Assert.isTrue(q == orderSize, "ok");
        Assert.isTrue(stock.getProductStock(PROD_ID) == stockSize - q, "ok");
    }

    @Test
    void givenStockFilled_whenExcessConsumed_thenCosumeLimited() {
        int stockSize = 10;
        int orderSize = 15;
        SimpleStock stock = new SimpleStock();
        stock.initStock(stockSize);

        int q = stock.consumeStock(PROD_ID, orderSize);

        Assert.isTrue(q == stockSize, "ok");
        Assert.isTrue(stock.getProductStock(PROD_ID) == 0, "ok");
    }

    @Test
    void givenStockFilled_whenConsumedAndRestored_thenStockFull() {
        int stockSize = 10;
        int orderSize = 5;
        SimpleStock stock = new SimpleStock();
        stock.initStock(stockSize);

        int q = stock.consumeStock(PROD_ID, orderSize);
        stock.refillStock(PROD_ID, q);

        Assert.isTrue(q == orderSize, "ok");
        Assert.isTrue(stock.getProductStock(PROD_ID) == stockSize, "ok");
    }
}
