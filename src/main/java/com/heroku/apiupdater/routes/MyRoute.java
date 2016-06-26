package com.heroku.apiupdater.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.rx.ReactiveCamel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;

@Component
public class MyRoute extends RouteBuilder {

    @Autowired
    public MyRoute(CamelContext context) {
        ReactiveCamel rx = new ReactiveCamel(context);
        Observable<String> fooObservable
                = rx.toObservable("direct:foo", String.class);

        Observable<String> barObservable
                = rx.toObservable("direct:bar", String.class);

        Observable<String> combineLatest
                = Observable.combineLatest(fooObservable, barObservable,
                        (foo, bar) -> {
                            return foo + bar;
                        });
        
        rx.sendTo(combineLatest, "direct:combine");
    }

    @Override
    public void configure() throws Exception {
        from("timer:foo?period=5s").setBody(constant("foo"))
                .filter((Exchange exchange) -> Math.random() > 0.7)
                .to("direct:foo");
        from("timer:bar?period=7s").setBody(constant("bar")).to("direct:bar");
        from("direct:combine").to("log:foobar");
    }

}
