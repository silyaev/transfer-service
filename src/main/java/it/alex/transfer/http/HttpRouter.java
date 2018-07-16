package it.alex.transfer.http;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import it.alex.transfer.config.AppContext;
import it.alex.transfer.exception.DataValuesValidationException;
import it.alex.transfer.exception.RequestValidationException;
import it.alex.transfer.model.AccountResponse;
import it.alex.transfer.model.ErrorResponse;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;
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
        final ExceptionHandler appExceptionHandler = ExceptionHandler.newBuilder()
                .match(RequestValidationException.class, x ->
                        complete(StatusCodes.BAD_REQUEST, x.getResponse(), Jackson.marshaller()))
                .match(DataValuesValidationException.class, ex -> complete(StatusCodes.BAD_REQUEST,
                        ErrorResponse.builder()
                                .description(ex.getMessage())
                                .code(StatusCodes.BAD_REQUEST.intValue())
                                .build(), Jackson.marshaller()))

                .match(Exception.class, ex -> complete(StatusCodes.INTERNAL_SERVER_ERROR,
                        ErrorResponse.builder()
                                .description(ex.getMessage())
                                .code(StatusCodes.INTERNAL_SERVER_ERROR.intValue())
                                .build(), Jackson.marshaller()))
                .build();

        return route(
                get(() ->
                        pathPrefix("account", () ->
                                path(longSegment(), (Long id) ->
                                        handleExceptions(appExceptionHandler, () -> {
                                            return getAccountRoute(id);
                                        })))),
                post(() ->
                        path("transfer", () ->
                                handleExceptions(appExceptionHandler, () ->
                                        entity(Jackson.unmarshaller(TransferRequest.class), order -> {
                                            CompletionStage<TransferResponse> transfer = apiService.moveMoney(order);
                                            return onSuccess(transfer, result ->
                                                    completeOK(result, Jackson.marshaller())
                                            );
                                        }))))
        );

    }

    private Route getAccountRoute(Long id) {
        final CompletionStage<Optional<AccountResponse>> account = apiService.getAccount(id);

        return onSuccess(account, maybeItem ->
                maybeItem.map(item -> completeOK(item, Jackson.marshaller()))
                        .orElseGet(() -> complete(StatusCodes.NOT_FOUND,
                                ErrorResponse.builder()
                                        .code(StatusCodes.NOT_FOUND.intValue())
                                        .description("Account not found")
                                        .request("account/" + id)
                                        .errors(Collections.emptyList())
                                        .build()
                                , Jackson.marshaller()))
        );
    }

}
