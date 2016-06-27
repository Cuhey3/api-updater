package com.heroku.apiupdater.definition.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import java.util.Date;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class SnapshotMongoClient {

  MongoClient client;
  String mongoUri, databaseName;

  @Autowired
  public SnapshotMongoClient(MongoConfig config) {
    mongoUri = config.snapshotMongoUri;
    databaseName = config.getDatabaseName(mongoUri);
    client = new MongoClient(new MongoClientURI(mongoUri));
  }

  public MongoClient get() {
    return client;
  }

  public void snapshotNow(String collectionName, Object object) {
    Document document;
    if (object instanceof Document) {
      document = (Document) object;
    } else {
      document = new Document();
      document.put("data", object);
    }
    document.put("creationDate", new Date());
    client.getDatabase(databaseName).getCollection(collectionName).insertOne(document);
  }
}
