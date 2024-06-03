package com.thinkitdevit.mongotrips.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    private ObjectId id;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private Integer availableSeats;
    private Integer bookedSeats;
    private String description;

}
