package com.example.fly3;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.File;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int price;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
    private File image;

    public Product(String name, int price, Category category, File image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }
}
