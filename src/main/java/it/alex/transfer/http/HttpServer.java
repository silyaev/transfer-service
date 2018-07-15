package it.alex.transfer.http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import it.alex.transfer.config.AppContext;
import it.alex.transfer.config.HttpConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
public class HttpServer {
    private final AppContext context;
    private final HttpConfig config;
    private final Route route;
    private volatile CompletionStage<ServerBinding> binding;

    public static HttpServer newInstance(final AppContext context, final HttpConfig config, final Route route) {
        final HttpServer httpServer = new HttpServer(context, config, route);
        httpServer.start();
        return httpServer;
    }

    public void stop() {
        log.info("Stopping Http Server");
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> log.info(" Http Server is stopped {}", unbound));
    }

    private void start() {
        log.info("Starting Http server use {}", this.config.getHttpConfig());

        final ActorSystem system = context.getSystem();
        final ActorMaterializer materializer = context.getMaterializer();

        final Http http = Http.get(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.flow(system, materializer);

        binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(config.getHost(), config.getPort()), materializer);

    }


}
