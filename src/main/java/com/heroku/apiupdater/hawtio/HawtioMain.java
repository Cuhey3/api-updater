package com.heroku.apiupdater.hawtio;

import io.hawt.embedded.Main;
import org.springframework.stereotype.Component;

@Component
public class HawtioMain {

    public HawtioMain() throws Exception {
        String env = System.getenv("MONGOLAB_URI");
        if (env == null) {
            env = System.getenv("API_UPDATER_MONGOLAB_URI");
        }
        String contextPath = env.split("/")[env.split("/").length - 1];
        Main main = new Main();
        System.setProperty("hawtio.authenticationEnabled", "false");
        String port = System.getenv("PORT");
        if (port == null) {
            port = "4646";
        }
        main.setPort(Integer.parseInt(port));
        main.setContextPath("/" + contextPath);
        main.setWarLocation("./");
        main.run();
    }
}
