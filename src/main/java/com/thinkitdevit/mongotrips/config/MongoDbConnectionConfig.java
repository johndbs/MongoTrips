package com.thinkitdevit.mongotrips.config;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDbConnectionConfig {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public static MongoDatabase getMongoDatabase() {
        String connectionString = System.getProperty("mongodb.uri");

        if(mongoClient == null) {
            mongoClient = MongoClients.create(connectionString);
        }
        if(mongoDatabase == null) {
            mongoDatabase = mongoClient.getDatabase("MongoTrips");
        }
        return mongoDatabase;
    }

    public static void closeConnection() {
        if(mongoClient != null) {
            mongoClient.close();
        }
    }

    public static ClientSession getClientSession(){
        return mongoClient.startSession();
    }

}
