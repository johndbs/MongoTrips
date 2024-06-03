package com.thinkitdevit.mongotrips.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.models.Booking;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookingService {

    public static final String COLLECTION_NAME = "bookings";
    private final MongoCollection<Document> bookingCollection;
    private final Mapper<Booking> bookingMapper;

    public BookingService(MongoDatabase database, Mapper<Booking> bookingMapper){
        this.bookingCollection = database.getCollection(COLLECTION_NAME);
        this.bookingMapper = bookingMapper;
    }

    /**
     * Create a new booking
     * @param booking
     */
    public void createBooking(Booking booking) {
        Document doc = bookingMapper.modelToDocument(booking);
        bookingCollection.insertOne(doc);
        booking.setId(doc.getObjectId("_id"));
    }

    /**
     * Get a booking by its ID
     * @param id The ID of the booking
     * @return The booking with the given ID
     */
    public Booking getBookingById(ObjectId id) {
        return bookingMapper.documentToModel(bookingCollection.find(Filters.eq("_id", id)).first());
    }

    /**
     * Get all bookings
     * @return All bookings
     */
    public List<Booking> getAllBookings() {
        return StreamSupport.stream(bookingCollection.find().spliterator(), false)
                .map(bookingMapper::documentToModel)
                .collect(Collectors.toList());
    }

    /**
     * Update a booking
     * @param booking The booking to update
     */
    public void update(Booking booking) {
        Bson filter = Filters.eq("_id", booking.getId());
        Bson update =  new Document("$set", bookingMapper.modelToDocument(booking));
        bookingCollection.updateOne(filter, update);
    }

    /**
     * Delete a booking
     * @param id The ID of the booking to delete
     */
    public void delete(ObjectId id) {
        bookingCollection.deleteOne(Filters.eq(id));
    }

}
