package com.example.fly3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long productId;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
