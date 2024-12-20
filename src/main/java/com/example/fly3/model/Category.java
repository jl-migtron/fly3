package com.example.fly3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Long parentCat;

    @OneToMany(mappedBy = "category")
    List<Product> products = new ArrayList();
}
