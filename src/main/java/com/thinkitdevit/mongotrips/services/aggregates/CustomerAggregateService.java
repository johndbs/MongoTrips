package com.thinkitdevit.mongotrips.services.aggregates;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.models.Customer;
import com.thinkitdevit.mongotrips.models.aggregates.CustomerAggregate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class CustomerAggregateService {


    public static final String COLLECTION_NAME = "customers";
    private final MongoCollection<Document> customerCollection;
    private final Mapper<Customer> customerMapper;
    private final Mapper<CustomerAggregate.BookingAggregate> bookingAggregateMapper;

    public CustomerAggregateService(MongoDatabase database, Mapper<Customer> customerMapper,
                                    Mapper<CustomerAggregate.BookingAggregate> bookingAggregateMapper){
        this.customerCollection = database.getCollection(COLLECTION_NAME);
        this.customerMapper = customerMapper;
        this.bookingAggregateMapper = bookingAggregateMapper;
    }


    public Optional<CustomerAggregate> getCustomerAggregate(ObjectId objectId){

        System.out.println("Search aggregate for customer: "+objectId);

        Bson match = Aggregates.match(Filters.eq("_id", objectId));

        Bson lookupBookings = Aggregates.lookup("bookings", "_id", "customerId", "bookings");

        Bson unwindBookings = Aggregates.unwind("$bookings");

        Bson lookupTrips = Aggregates.lookup("trips", "bookings.tripId", "_id", "bookings.tripDetails");

        Bson unwindBookingsTripDetails = Aggregates.unwind("$bookings.tripDetails");

        Bson groupTripDetails = Aggregates.group("$_id",
                new BsonField("firstName", new Document("$first", "$firstName")),
                new BsonField("lastName", new Document("$first", "$lastName")),
                new BsonField("email", new Document("$first", "$email")),
                new BsonField("phoneNumber", new Document("$first", "$phoneNumber")),
                new BsonField("address", new Document("$first", "$address")),
                new BsonField("bookings", new Document("$push", "$bookings"))
                );

        Bson projectTripDetails = Aggregates.project(Projections.fields(
                Projections.include("_id", "firstName", "lastName", "email", "phoneNumber", "address", "bookings")
        ));



        List<Bson> pipeline = List.of(match, lookupBookings,
                unwindBookings, lookupTrips,
                unwindBookingsTripDetails, groupTripDetails, projectTripDetails
        );
        AggregateIterable<Document> aggregateResult = customerCollection.aggregate(pipeline);

        Document explainResult = customerCollection.aggregate(pipeline).explain();
        System.out.println(explainResult.toJson());


        return Optional.ofNullable(aggregateResult.first())
                .map(this::documentToCustomerAggregate);

    }


    private CustomerAggregate documentToCustomerAggregate(Document document){


        List<CustomerAggregate.BookingAggregate> bookingAggregates = Optional.of(document.get("bookings"))
                .map((doc) -> (List<Document>)doc)
                .orElse(List.of())
                .stream()
                .map(bookingAggregateMapper::documentToModel)
                .collect(Collectors.toList());

        CustomerAggregate customerAggregate = CustomerAggregate.builder()
                .customer(customerMapper.documentToModel(document))
                .bookings(bookingAggregates)
                .build();

        return customerAggregate;
    }

}
