package com.thinkitdevit.mongotrips.mapper;

import com.thinkitdevit.mongotrips.models.Trip;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;

public class TripMapper extends Mapper<Trip> {

    @Override
    public Document modelToDocument(Trip trip){
        Document doc = new Document();
        if(trip.getId()!=null){
            doc.append("_id", trip.getId());
        }
        doc.append("destination", trip.getDestination());
        doc.append("startDate",trip.getStartDate());
        doc.append("endDate",trip.getEndDate());
        doc.append("price",trip.getPrice());
        doc.append("availableSeats",trip.getAvailableSeats());
        doc.append("bookedSeats",trip.getBookedSeats());
        doc.append("description",trip.getDescription());
        return doc;
    }

    @Override
    public Trip documentToModel(Document document){
        return Trip.builder()
                .id(document.getObjectId("_id"))
                .destination(document.getString("destination"))
                .startDate(getLocalDate(document, "startDate"))
                .endDate(getLocalDate(document,"endDate"))
                .price(document.getDouble("price"))
                .availableSeats(document.getInteger("availableSeats"))
                .bookedSeats(document.getInteger("bookedSeats"))
                .description(document.getString("description"))
                .build();
    }



}
