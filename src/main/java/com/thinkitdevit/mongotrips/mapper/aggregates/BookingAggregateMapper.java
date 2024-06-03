package com.thinkitdevit.mongotrips.mapper.aggregates;

import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.models.aggregates.CustomerAggregate;
import org.bson.Document;

import java.util.Optional;

public class BookingAggregateMapper extends Mapper<CustomerAggregate.BookingAggregate> {
    @Override
    public Document modelToDocument(CustomerAggregate.BookingAggregate model) {
        return null;
    }

    @Override
    public CustomerAggregate.BookingAggregate documentToModel(Document document) {

        CustomerAggregate.BookingAggregate bookingAggregate = CustomerAggregate.BookingAggregate.builder()
                .id(document.getObjectId("_id"))
                .date(getLocalDateTime(document, "date"))
                .status(document.getString("status"))
                .build();

        Optional.ofNullable((Document)document.get("tripDetails"))
                .ifPresent(tripDetailsDoc -> {
                    bookingAggregate.setStartDate(getLocalDate(tripDetailsDoc, "startDate"));
                    bookingAggregate.setEndDate(getLocalDate(tripDetailsDoc, "endDate"));
                    bookingAggregate.setPrice(tripDetailsDoc.getDouble("price"));
                    bookingAggregate.setDestination(tripDetailsDoc.getString("destination"));
                    bookingAggregate.setDescription(tripDetailsDoc.getString("description"));
                });

        return bookingAggregate;
    }
}
