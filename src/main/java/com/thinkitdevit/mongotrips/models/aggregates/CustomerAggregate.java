package com.thinkitdevit.mongotrips.models.aggregates;

import com.thinkitdevit.mongotrips.models.Booking;
import com.thinkitdevit.mongotrips.models.Customer;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@ToString
public class CustomerAggregate {

    private Customer customer;

    private List<BookingAggregate> bookings;


    @Data
    @AllArgsConstructor
    @Builder
    @ToString
    public static class BookingAggregate{

        private ObjectId id;
        private LocalDateTime date;
        private String status;

        private LocalDate startDate;
        private LocalDate endDate;
        private Double price;
        private String destination;
        private String description;

    }

}
