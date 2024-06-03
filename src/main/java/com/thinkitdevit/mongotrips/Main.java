package com.thinkitdevit.mongotrips;

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
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        MongoDatabase mongoDatabase = MongoDbConnectionConfig.getMongoDatabase();

        Mapper<Trip> tripMapper = new TripMapper();
        TripService tripService = new TripService(mongoDatabase, tripMapper);

        Mapper<Customer>  customerMapper = new CustomerMapper();
        CustomerService customerService = new CustomerService(mongoDatabase, customerMapper);

        Mapper<Booking> bookingMapper = new BookingMapper();
        BookingService bookingService = new BookingService(mongoDatabase, bookingMapper);

        Mapper<CustomerAggregate.BookingAggregate> bookingAggregateMapper = new BookingAggregateMapper();
        CustomerAggregateService customerAggregateService = new CustomerAggregateService(mongoDatabase,
                customerMapper, bookingAggregateMapper);


        // Create a trip
        Trip tripParis = Trip.builder()
                .destination("Paris")
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 6, 10))
                .price(2041.99 )
                .availableSeats(10)
                .bookedSeats(0)
                .description("A trip to Paris")
                .build();

        tripService.createTrip(tripParis);

        Trip tripLondon = Trip.builder()
                .destination("London")
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2024, 7, 10))
                .price(1900.99 )
                .availableSeats(60)
                .bookedSeats(0)
                .description("A trip to London")
                .build();

        tripService.createTrip(tripLondon);

        // Read a trip by ID
        Trip tripById = tripService.getTripById(tripParis.getId());
        System.out.println("Trip by ID: " + tripById + " description: "+ tripById.getDescription());

        // Update the trip
        tripParis.setDescription("A beautiful trip to Paris");
        tripService.update(tripParis);

        // Read
        tripById = tripService.getTripById(tripParis.getId());
        System.out.println("Trip by ID: " + tripById + " description: "+ tripById.getDescription());

        // Delete the trip
        tripService.delete(tripLondon.getId());

        // Get all trips
        tripService.getAllTrips().forEach(System.out::println);



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

        customerService.createCustomer(customerAlice);

        Customer customerFound = customerService.getCustomerById(customerAlice.getId());

        System.out.println("Customer by ID: "+customerFound.getId() + " firstName: "+customerFound.getFirstName());

        // Create an other customer
        Customer customerBob = Customer.builder()
                .firstName("Bob")
                .lastName("Iron")
                .email("bob.iron@test.com")
                .phoneNumber("0123456789")
                .build();

        customerService.createCustomer(customerBob);

        // Get customer by ID
        customerFound = customerService.getCustomerById(customerBob.getId());
        System.out.println("Customer by ID: "+customerFound.getId() + " firstName: "+customerFound.getFirstName());

        // Delete a customer
        customerService.delete(customerBob.getId());

        // Get all customer
        customerService.getAllCustomers().forEach(System.out::println);


        // Create a booking

        Booking bookingAliceToParis = Booking.builder()
                .date(LocalDateTime.of(2024, 6, 1, 18, 0))
                .status(Booking.Status.CONFIRMED)
                .customerId(customerAlice.getId())
                .tripId(tripParis.getId())
                .build();

        bookingService.createBooking(bookingAliceToParis);

        // Get booking by ID
        Booking bookingFound = bookingService.getBookingById(bookingAliceToParis.getId());

        System.out.println("Booking ID: "+bookingFound.getId() + " customerId: "+bookingFound.getCustomerId()+ " tripId: "+ bookingFound.getTripId());


        // Get all booking
        bookingService.getAllBookings().forEach(System.out::println);


        // Delete a booking
        //bookingService.delete(bookingAliceToParis.getId());


        customerAggregateService.getCustomerAggregate(customerAlice.getId())
                .ifPresentOrElse(System.out::println,
                        () -> System.out.println("Booking aggregate not found"));




        // Close the connection
        MongoDbConnectionConfig.closeConnection();
    }


}
