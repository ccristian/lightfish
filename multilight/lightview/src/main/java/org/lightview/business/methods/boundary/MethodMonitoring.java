package org.lightview.business.methods.boundary;

import org.lightview.business.methods.entity.MethodStatistics;
import org.lightview.business.methods.entity.MethodsStatistics;
import org.lightview.presentation.dashboard.DashboardModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.function.Consumer;

/**
 * @author: adam-bien.com
 */
public class MethodMonitoring {
    private Client client;

    @Inject
    DashboardModel model;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient();
    }

    public void listenToMethodStatistics(Consumer<MethodsStatistics> consumer, Consumer<Throwable> error, String application, String ejbName) {
        final String uri = getUri();
        System.out.println("Uri: " + uri);
        WebTarget target = this.client.target(uri);

        target.
                resolveTemplate("application", application).
                resolveTemplate("ejb", ejbName).
                request(MediaType.APPLICATION_JSON).async().get(new InvocationCallback<JsonObject>() {
            @Override
            public void completed(JsonObject jsonObject) {
                consumer.accept(new MethodsStatistics(jsonObject));
            }

            @Override
            public void failed(Throwable throwable) {
                error.accept(throwable);
            }
        });

    }

    public String getUri() {
        return model.serverUriProperty().get() + "/resources/applications/{application}/ejbs/{ejb}";
    }
}