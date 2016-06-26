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
    public Document ownSettings;
    public Document masterSettings;

    public MongoConfig() {
        ownMongoURI = System.getenv("MONGODB_URI");
        if (ownMongoURI == null) {
            ownMongoURI = System.getenv("API_UPDATER_MONGOLAB_URI");
        }
        if (ownMongoURI != null) {
            ownMongoDatabase = getDatabaseName(ownMongoURI);
        }
        ownSettings = getSettings(ownMongoURI);
    }

    @Bean(name = "api-updater")
    MongoClient apiUpdaterMongoClient() throws UnknownHostException {
        return new MongoClient(new MongoClientURI(ownMongoURI));
    }

    @Bean(name = "master")
    MongoClient masterMongoClient() throws UnknownHostException {
        String masterMongoUri
                = ownSettings.get("MASTER_MONGOLAB_URI", String.class);

        masterSettings = getSettings(masterMongoUri);
        return new MongoClient(new MongoClientURI(masterMongoUri));
    }

    private String getDatabaseName(String mongoUri) {
        String[] split = mongoUri.split("/");
        return split[split.length - 1];
    }

    public final Document getSettings(String mongoUri) {
        try (MongoClient mongoClient
                = new MongoClient(new MongoClientURI(mongoUri))) {

            FindIterable<Document> find
                    = mongoClient.getDatabase(getDatabaseName(mongoUri))
                    .getCollection("settings").find();

            if (find.iterator().hasNext()) {
                return find.first();
            }
        }
        return null;
    }
}
