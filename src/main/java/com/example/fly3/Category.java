package com.example.fly3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@Entity
@Data
@NoArgsConstructor
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private Long parentCat;

    public Category(String name, Long parentCat) {
        this.name = name;
        this.parentCat = parentCat;
    }
}
