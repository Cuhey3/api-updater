package com.heroku.apiupdater.definition;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import java.net.UnknownHostException;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    public String ownMongoURI;
    public String ownMongoDatabase;
    public Document settings;

    public MongoConfig() {
        ownMongoURI = System.getenv("MONGODB_URI");
        if (ownMongoURI == null) {
            ownMongoURI = System.getenv("API_UPDATER_MONGOLAB_URI");
        }
        if (ownMongoURI != null) {
            String[] split = ownMongoURI.split("/");
            ownMongoDatabase = split[split.length - 1];
        }
        try (MongoClient mongoClient
                = new MongoClient(new MongoClientURI(ownMongoURI))) {

            FindIterable<Document> find
                    = mongoClient.getDatabase(ownMongoDatabase)
                    .getCollection("settings").find();

            if (find.iterator().hasNext()) {
                settings = find.first();
            }
        }
    }

    @Bean(name = "api-updater")
    MongoClient mongoClientApiUpdater() throws UnknownHostException {
        return new MongoClient(new MongoClientURI(ownMongoURI));
    }
}
