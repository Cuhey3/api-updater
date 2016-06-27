package com.heroku.apiupdater.definition.mongo;

import com.heroku.apiupdater.definition.mongo.MongoConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MongoCollections {

  @Autowired
  public MongoCollections(MongoConfig config) {
    createSnapshotCollections(config);
  }

  private void createSnapshotCollections(MongoConfig config) {
    MongoDatabase snapshotDatabase
            = config.snapshotClient.getDatabase(
                    config.getDatabaseName(config.snapshotMongoUri));

    Set<String> existCollectionNames
            = StreamSupport.stream(
                    snapshotDatabase.listCollectionNames().spliterator(), false)
            .collect(Collectors.toSet());

    Document settings = config.getMasterSettings();
    List<Map<String, Object>> definedCollections
            = settings.get("collections", List.class);

    definedCollections.stream()
            .map((collection) -> (String) collection.get("collectionName"))
            .filter((collectionName)
                    -> (!existCollectionNames.contains("snapshot_" + collectionName)))
            .forEach((collectionName) -> {
              snapshotDatabase.createCollection("snapshot_" + collectionName,
                      new CreateCollectionOptions()
                      .sizeInBytes(16777216).capped(true).maxDocuments(50));
            });
  }
}
