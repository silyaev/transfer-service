package it.alex.transfer.http;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import it.alex.transfer.config.AppContext;
import it.alex.transfer.model.Account;
import it.alex.transfer.model.ErrorResponse;
import it.alex.transfer.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.longSegment;

@Slf4j
@RequiredArgsConstructor
public class HttpRouter extends AllDirectives {
    private final AppContext context;
    private ApiService apiService;

    public static HttpRouter newInstance(final AppContext context) {
        final HttpRouter httpRouter = new HttpRouter(context);
        httpRouter.init();
        return httpRouter;
    }

    public void init() {
        apiService = Optional.ofNullable(context.getApiService())
                .orElseThrow(() -> new IllegalStateException("API Service not found"));
    }

    public Route createRoute() {

        return route(
                get(() ->
                        pathPrefix("account", () ->
                                path(longSegment(), (Long id) -> {
                                    final CompletionStage<Optional<Account>> futureMaybeItem = apiService.getAccount(id);
                                    return onSuccess(futureMaybeItem, maybeItem ->
                                            maybeItem.map(item -> completeOK(item, Jackson.marshaller()))
                                                    .orElseGet(() -> complete(StatusCodes.NOT_FOUND, ErrorResponse.builder()
                                                                    .code(StatusCodes.NOT_FOUND.intValue())
                                                                    .description("Account not found")
                                                                    .request("account/" + id)
                                                                    .errors(Collections.emptyList())
                                                                    .build()
                                                            , Jackson.marshaller()))
                                    );
                                })))
        );

    }

}
