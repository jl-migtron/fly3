package com.example.fly3;

/**
 *
 * @author jluis.albarral@gmail.com
 */
public class ProductNotFoundException extends RuntimeException {

    ProductNotFoundException(Long id) {
        super("Could not find product " + id);
    }
}
