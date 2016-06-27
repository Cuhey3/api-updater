package com.heroku.apiupdater.definition.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

  public String ownMongoUri, masterMongoUri, snapshotMongoUri;
  public Document ownSettings, masterSettings;
  public MongoClient snapshotClient;

  public MongoConfig() {
    ownMongoUri = System.getenv("MONGODB_URI");
    if (ownMongoUri == null) {
      ownMongoUri = System.getenv("API_UPDATER_MONGOLAB_URI");
    }
    ownSettings = getSettings(ownMongoUri);
    masterMongoUri
            = ownSettings.get("MASTER_MONGOLAB_URI", String.class);

    masterSettings = getSettings(masterMongoUri);

    snapshotMongoUri
            = masterSettings.get("SNAPSHOT_MONGOLAB_URI", String.class);

    snapshotClient = new MongoClient(new MongoClientURI(snapshotMongoUri));
  }

  @Bean(name = "master")
  public MongoClient masterMongoClient() {
    return new MongoClient(new MongoClientURI(masterMongoUri));
  }

  @Bean(name = "snapshot")
  public MongoClient snapshotMongoClient() {
    return snapshotClient;
  }

  public final String getDatabaseName(String mongoUri) {
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

  public Document getMasterSettings() {
    return masterSettings;
  }
}
