package com.heroku.apiupdater.sources.polling;

import com.heroku.apiupdater.definition.mongo.MongoConfig;
import com.heroku.apiupdater.sources.content.MediawikiApiRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaleSeiyuCategoryMembers extends RouteBuilder {

  final String collectionName = "snapshot_male_seiyu_category_members";
  @Autowired
  MongoConfig config;

  @Override
  public void configure() throws Exception {
    fromF("timer:%s?period=191s&delay=191s", collectionName)
            .routeId(collectionName)
            .process((Exchange exchange) -> {
              List<Map<String, Object>> mapList
                      = new MediawikiApiRequest()
                      .setApiParam("action=query&list=categorymembers"
                              + "&cmtitle=Category:%E6%97%A5%E6%9C%AC%E3%81%AE%E7%94%B7%E6%80%A7%E5%A3%B0%E5%84%AA"
                              + "&cmlimit=500"
                              + "&cmnamespace=0"
                              + "&format=xml"
                              + "&continue="
                              + "&cmprop=title|ids|sortkeyprefix")
                      .setListName("categorymembers").setMapName("cm")
                      .setContinueElementName("cmcontinue")
                      .setIgnoreFields("ns")
                      .getResultByMapList();

              mapList.forEach((m) -> m.put("gender", "m"));
              Document document = new Document();
              document.put("data", mapList);
              document.put("creationDate", new Date());
              exchange.getIn().setBody(document);
            })
            .toF("mongodb:snapshot?database=%s&collection=%s&operation=insert",
                    config.snapshotDatabaseName,
                    collectionName);
  }
}
