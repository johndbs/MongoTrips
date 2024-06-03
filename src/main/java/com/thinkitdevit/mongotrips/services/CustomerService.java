package com.thinkitdevit.mongotrips.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.thinkitdevit.mongotrips.mapper.Mapper;
import com.thinkitdevit.mongotrips.models.Customer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CustomerService {

    public static final String COLLECTION_NAME = "customers";

    private final MongoCollection<Document> customerCollection;
    private final Mapper<Customer> customerMapper;

    public CustomerService(MongoDatabase database, Mapper<Customer> customerMapper){
        this.customerCollection = database.getCollection(COLLECTION_NAME);
        this.customerMapper = customerMapper;
    }

    /**
     * Create a new customer
     * @param customer
     */
    public void createCustomer(Customer customer) {
        Document doc = customerMapper.modelToDocument(customer);
        customerCollection.insertOne(doc);

        customer.setId(doc.getObjectId("_id"));
    }

    /**
     * Get a customer by its ID
     * @param id The ID of the customer
     * @return The customer with the given ID
     */
    public Customer getCustomerById(ObjectId id) {
        return customerMapper.documentToModel(customerCollection.find(new Document("_id", id)).first());
    }

    /**
     * Get all customers
     * @return All customers
     */
    public List<Customer> getAllCustomers() {
        return StreamSupport.stream(customerCollection.find().spliterator(), false)
                .map(customerMapper::documentToModel)
                .collect(Collectors.toList());
    }

    /**
     * Update a customer
     * @param customer The customer to update
     */
    public void update(Customer customer) {
        Bson find = Filters.eq(customer.getId());
        Bson update = new Document("$set", customerMapper.modelToDocument(customer));
        customerCollection.updateOne(find, update);
    }

    /**
     * Delete a customer
     * @param id The ID of the customer to delete
     */
    public void delete(ObjectId id) {
        customerCollection.deleteOne(Filters.eq("_id", id));
    }

}
