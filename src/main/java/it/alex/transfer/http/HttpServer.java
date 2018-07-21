package it.alex.transfer.http;

import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import it.alex.transfer.config.AppContext;
import it.alex.transfer.config.HttpConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public class HttpServer extends HttpApp {
    private final AppContext context;
    private final HttpConfig config;
    private final Route route;

    public static HttpServer newInstance(final AppContext context, final HttpConfig config, final Route route) {
        return new HttpServer(context, config, route);
    }

    public void start() throws ExecutionException, InterruptedException {
        this.startServer(config.getHost(), config.getPort(), context.getSystem());

    }

    @Override
    protected Route routes() {
        return route;
    }
}
