package com.heroku.apiupdater.definition.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
public class MasterMongoClient {

  MongoClient client;
  String mongoUri, databaseName;

  @Autowired
  public MasterMongoClient(MongoConfig config) {
    mongoUri = config.masterMongoUri;
    databaseName = config.getDatabaseName(mongoUri);
    client = new MongoClient(new MongoClientURI(mongoUri));
  }

  public MongoClient get() {
    return client;
  }
}
