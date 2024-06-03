package com.thinkitdevit.mongotrips.services;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.thinkitdevit.mongotrips.exception.SelloutException;
import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.models.Booking;
import com.thinkitdevit.mongotrips.models.Trip;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookingService {

    public static final String COLLECTION_NAME = "bookings";
    private final MongoCollection<Document> bookingCollection;
    private final Mapper<Booking> bookingMapper;

    private final TripService tripService;

    public BookingService(MongoDatabase database, Mapper<Booking> bookingMapper, TripService tripService){
        this.bookingCollection = database.getCollection(COLLECTION_NAME);
        this.bookingMapper = bookingMapper;
        this.tripService = tripService;
    }

    /**
     * Create a new booking
     * @param booking
     */
    public void createBooking(ClientSession clientSession, Booking booking) {
        Document doc = bookingMapper.modelToDocument(booking);
        bookingCollection.insertOne(clientSession, doc);
        booking.setId(doc.getObjectId("_id"));
    }

    /**
     * Get a booking by its ID
     * @param id The ID of the booking
     * @return The booking with the given ID
     */
    public Booking getBookingById(ClientSession clientSession, ObjectId id) {
        return bookingMapper.documentToModel(bookingCollection.find(clientSession, Filters.eq("_id", id)).first());
    }

    /**
     * Get all bookings
     * @return All bookings
     */
    public List<Booking> getAllBookings(ClientSession clientSession) {
        return StreamSupport.stream(bookingCollection.find(clientSession).spliterator(), false)
                .map(bookingMapper::documentToModel)
                .collect(Collectors.toList());
    }

    /**
     * Update a booking
     * @param booking The booking to update
     */
    public void update(ClientSession clientSession, Booking booking) {
        Bson filter = Filters.eq("_id", booking.getId());
        Bson update =  new Document("$set", bookingMapper.modelToDocument(booking));
        bookingCollection.updateOne(clientSession, filter, update);
    }

    /**
     * Delete a booking
     * @param id The ID of the booking to delete
     */
    public void delete(ClientSession clientSession, ObjectId id) {
        bookingCollection.deleteOne(clientSession, Filters.eq(id));
    }

    public Booking book(ClientSession clientSession, ObjectId tripId, ObjectId customerId) {


        Trip trip = tripService.getTripById(clientSession, tripId);

        trip.setBookedSeats(trip.getBookedSeats() + 1);

        tripService.update(clientSession, trip);


        Booking booking = Booking.builder()
                .date(LocalDateTime.now())
                .status(Booking.Status.PENDING)
                .customerId(customerId)
                .tripId(tripId)
                .build();

        createBooking(clientSession,booking);

        if(trip.getBookedSeats() > trip.getAvailableSeats()){
            throw new SelloutException("All seats are sellout tripId:"+tripId);
        }


        return booking;
    }
}
