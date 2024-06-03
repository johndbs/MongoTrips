package com.thinkitdevit.mongotrips.services;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.thinkitdevit.mongotrips.dto.PageRequest;
import com.thinkitdevit.mongotrips.dto.PageResponse;
import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.mapper.TripMapper;
import com.thinkitdevit.mongotrips.models.Trip;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TripService {

    public static final String COLLECTION_NAME = "trips";
    private final MongoCollection<Document> tripCollection;

    private final Mapper<Trip> tripMapper;


    public TripService(MongoDatabase mongoDatabase, Mapper<Trip>  tripMapper) {
        this.tripCollection = mongoDatabase.getCollection(COLLECTION_NAME);
        this.tripMapper = tripMapper;
    }

    /**
     * Add a new trip to the database
     * @param trip The trip to add
     */
    public void createTrip(ClientSession clientSession, Trip trip) {
        Document doc = tripMapper.modelToDocument(trip);
        tripCollection.insertOne(clientSession, doc);

        trip.setId(doc.getObjectId("_id"));
    }

    /**
     * Get a trip by its ID
     * @param id The ID of the trip
     * @return The trip with the given ID
     */
    public Trip getTripById(ClientSession clientSession, ObjectId id) {
        Document docFound = tripCollection.find(clientSession, Filters.eq("_id", id)).first();
        return tripMapper.documentToModel(docFound);
    }

    /**
     * Get all trips
     * @return All trips
     */
    public List<Trip> getAllTrips(ClientSession clientSession) {
        FindIterable<Document> documentFindIterable = tripCollection.find(clientSession);

        return StreamSupport.stream(documentFindIterable.spliterator(), false)
                .map(tripMapper::documentToModel)
                .collect(Collectors.toList());

    }

    /**
     * Update a trip
     * @param trip The trip to update
     */
    public void update(ClientSession clientSession, Trip trip) {
        Bson find = Filters.eq("_id", trip.getId());
        Document update = new Document("$set", tripMapper.modelToDocument(trip));
        tripCollection.updateOne(clientSession, find, update);
    }

    /**
     * Delete a trip
     * @param id The ID of the trip to delete
     */
    public void delete(ClientSession clientSession, ObjectId id) {
       tripCollection.deleteOne(clientSession, Filters.eq("_id", id));
    }

    /**
     * Get a paginated list of trips
     * @param pageRequest The page request
     * @return A paginated list of trips
     */
    public PageResponse<Trip> getPaginatedTrips(PageRequest pageRequest){
        // TODO
        return null;
    }

}
