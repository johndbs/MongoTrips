package com.thinkitdevit.mongotrips.mapper;

import com.thinkitdevit.mongotrips.models.Customer;
import org.bson.Document;

import java.util.Optional;

public class CustomerMapper extends Mapper<Customer> {

    @Override
    public Document modelToDocument(Customer model) {

        Document doc = new Document();

        Optional.ofNullable(model.getId())
                .ifPresent(id -> doc.append("_id",id));

        doc.append("firstName", model.getFirstName());
        doc.append("lastName", model.getLastName());
        doc.append("email", model.getEmail());
        doc.append("phoneNumber", model.getPhoneNumber());

        addressToDocument(Optional.ofNullable(model.getAddress()))
                .ifPresent(modelAddress -> doc.append("address", modelAddress));

        return doc;
    }

    @Override
    public Customer documentToModel(Document document) {

        Customer customer = Customer.builder()
                .id(document.getObjectId("_id"))
                .firstName(document.getString("firstName"))
                .lastName(document.getString("lastName"))
                .email(document.getString("email"))
                .phoneNumber(document.getString("phoneNumber"))
                .build();

        Optional.ofNullable(document.get("address"))
                .ifPresent(docAddress -> customer.setAddress(documentToAddress((Document) docAddress)));

        return customer;
    }


    private Optional<Document> addressToDocument(Optional<Customer.Address> optionalModel){

        return optionalModel.map(model -> {
            Document doc = new Document();
            doc.append("street", model.getStreet());
            doc.append("city", model.getCity());
            doc.append("state", model.getState());
            doc.append("zipCode", model.getZipCode());
            doc.append("country", model.getCountry());
            return doc;
        });
    }

    private Customer.Address documentToAddress(Document doc) {
        return Customer.Address.builder()
                        .street(doc.getString("street"))
                        .city(doc.getString("city"))
                        .state(doc.getString("state"))
                        .zipCode(doc.getString("zipCode"))
                        .country(doc.getString("country"))
                        .build();
    }


}
