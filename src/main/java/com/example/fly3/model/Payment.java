package com.example.fly3.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jluis.albarral@gmail.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Payment {

    private String card;
    @Enumerated(EnumType.STRING)
    private PaymentStatus payStatus;
    private Date date;
    private String gateway;
}
