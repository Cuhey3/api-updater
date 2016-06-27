package com.heroku.apiupdater.hawtio;

import com.heroku.apiupdater.definition.mongo.MongoConfig;
import io.hawt.embedded.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HawtioMain {

  @Autowired
  public HawtioMain(MongoConfig config) throws Exception {
    Main main = new Main();
    System.setProperty("hawtio.authenticationEnabled", "false");
    String port = System.getenv("PORT");
    if (port == null) {
      port = "4646";
    }
    main.setPort(Integer.parseInt(port));
    main.setContextPath("/" + config.ownMongoDatabaseName);
    main.setWarLocation("./");
    main.run();
  }
}
