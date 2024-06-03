package com.thinkitdevit.mongotrips;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import com.thinkitdevit.mongotrips.config.MongoDbConnectionConfig;
import com.thinkitdevit.mongotrips.mapper.BookingMapper;
import com.thinkitdevit.mongotrips.mapper.CustomerMapper;
import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.mapper.TripMapper;
import com.thinkitdevit.mongotrips.mapper.aggregates.BookingAggregateMapper;
import com.thinkitdevit.mongotrips.models.Booking;
import com.thinkitdevit.mongotrips.models.Customer;
import com.thinkitdevit.mongotrips.models.Trip;
import com.thinkitdevit.mongotrips.models.aggregates.CustomerAggregate;
import com.thinkitdevit.mongotrips.services.BookingService;
import com.thinkitdevit.mongotrips.services.CustomerService;
import com.thinkitdevit.mongotrips.services.TripService;
import com.thinkitdevit.mongotrips.services.aggregates.CustomerAggregateService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class Main {

    public static void main(String[] args) {

        MongoDatabase mongoDatabase = MongoDbConnectionConfig.getMongoDatabase();

        Mapper<Trip> tripMapper = new TripMapper();
        TripService tripService = new TripService(mongoDatabase, tripMapper);

        Mapper<Customer>  customerMapper = new CustomerMapper();
        CustomerService customerService = new CustomerService(mongoDatabase, customerMapper);

        Mapper<Booking> bookingMapper = new BookingMapper();
        BookingService bookingService = new BookingService(mongoDatabase, bookingMapper, tripService);

        Mapper<CustomerAggregate.BookingAggregate> bookingAggregateMapper = new BookingAggregateMapper();
        CustomerAggregateService customerAggregateService = new CustomerAggregateService(mongoDatabase,
                customerMapper, bookingAggregateMapper);

        ClientSession clientSession = MongoDbConnectionConfig.getClientSession();

        // Create a trip
        Trip tripParis = Trip.builder()
                .destination("Paris")
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 6, 10))
                .price(2041.99 )
                .availableSeats(1)
                .bookedSeats(0)
                .description("A trip to Paris")
                .build();

        tripService.createTrip(clientSession, tripParis);

        Trip tripLondon = Trip.builder()
                .destination("London")
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2024, 7, 10))
                .price(1900.99 )
                .availableSeats(60)
                .bookedSeats(0)
                .description("A trip to London")
                .build();

        tripService.createTrip(clientSession, tripLondon);

        // Read a trip by ID
        Trip tripById = tripService.getTripById(clientSession, tripParis.getId());
        System.out.println("Trip by ID: " + tripById + " description: "+ tripById.getDescription());

        // Update the trip
        tripParis.setDescription("A beautiful trip to Paris");
        tripService.update(clientSession, tripParis);

        // Read
        tripById = tripService.getTripById(clientSession, tripParis.getId());
        System.out.println("Trip by ID: " + tripById + " description: "+ tripById.getDescription());

        // Delete the trip
        tripService.delete(clientSession, tripLondon.getId());

        // Get all trips
        tripService.getAllTrips(clientSession).forEach(System.out::println);



        // Create a customer

        Customer customerAlice = Customer.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@test.com")
                .phoneNumber("0123456789")
                .address(Customer.Address.builder()
                        .street("1 street")
                        .city("city")
                        .zipCode("10000")
                        .country("FR")
                        .build())
                .build();

        customerService.createCustomer(clientSession, customerAlice);

        Customer customerFound = customerService.getCustomerById(clientSession, customerAlice.getId());

        System.out.println("Customer by ID: "+customerFound.getId() + " firstName: "+customerFound.getFirstName());

        // Create an other customer
        Customer customerBob = Customer.builder()
                .firstName("Bob")
                .lastName("Iron")
                .email("bob.iron@test.com")
                .phoneNumber("0123456789")
                .build();

        customerService.createCustomer(clientSession, customerBob);

        // Get customer by ID
        customerFound = customerService.getCustomerById(clientSession, customerBob.getId());
        System.out.println("Customer by ID: "+customerFound.getId() + " firstName: "+customerFound.getFirstName());

        // Delete a customer
        customerService.delete(clientSession, customerBob.getId());


        // Create an other customer
        Customer customerCarl = Customer.builder()
                .firstName("Carl")
                .lastName("Iron")
                .email("carl.iron@test.com")
                .phoneNumber("0123456789")
                .build();

        customerService.createCustomer(clientSession, customerCarl);

        // Get all customer
        customerService.getAllCustomers(clientSession).forEach(System.out::println);



        // Create a first booking with transaction

        Booking createdBooking = null;


        try{
            clientSession.startTransaction();

            createdBooking = bookingService.book(clientSession, tripParis.getId(), customerAlice.getId());

            clientSession.commitTransaction();
        }catch (Exception e){
            log.error("Transaction fail : "+e);
            clientSession.abortTransaction();
        }


        // Get booking by ID
        Booking bookingFound = bookingService.getBookingById(clientSession, createdBooking.getId());

        System.out.println("Booking ID: "+bookingFound.getId() + " customerId: "+bookingFound.getCustomerId()+ " tripId: "+ bookingFound.getTripId());


        // Create a second booking with transaction

        createdBooking = null;

        try{
            clientSession.startTransaction();

            createdBooking = bookingService.book(clientSession, tripParis.getId(), customerCarl.getId());

            clientSession.commitTransaction();
        }catch (Exception e){
            System.out.println("Transcation fail : "+e);
            clientSession.abortTransaction();
        }



        // Get all booking
        bookingService.getAllBookings(clientSession).forEach(System.out::println);


        // Delete a booking
        //bookingService.delete(bookingAliceToParis.getId());


        customerAggregateService.getCustomerAggregate(clientSession, customerAlice.getId())
                .ifPresentOrElse(System.out::println,
                        () -> System.out.println("Booking aggregate not found"));



        clientSession.close();

        // Close the connection
        MongoDbConnectionConfig.closeConnection();
    }


}
