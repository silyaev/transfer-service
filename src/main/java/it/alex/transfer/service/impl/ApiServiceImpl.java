package it.alex.transfer.service.impl;

import it.alex.transfer.config.AppContext;
import it.alex.transfer.model.AccountResponse;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;
import it.alex.transfer.service.AccountInfoService;
import it.alex.transfer.service.ApiService;
import it.alex.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.ExecutionContextExecutor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

    private final AppContext context;

    private TransferService transferService;
    private AccountInfoService accountInfoService;
    private ExecutionContextExecutor dispatcher;

    public static ApiServiceImpl newInstance(final AppContext context) {
        final ApiServiceImpl apiService = new ApiServiceImpl(context);
        apiService.init();
        return apiService;
    }

    public CompletionStage<TransferResponse> moveMoney(TransferRequest request) {
        return CompletableFuture.supplyAsync(() -> transferService.moveMoney(request), dispatcher);
    }

    public CompletionStage<Optional<AccountResponse>> getAccount(Long id) {
        return CompletableFuture.supplyAsync(() -> accountInfoService.findAccount(id), dispatcher);
    }

    private void init() {
        transferService = Optional.ofNullable(context.getTransferService())
                .orElseThrow(() -> new IllegalStateException("transfer Service not found"));

        accountInfoService = Optional.ofNullable(context.getAccountInfoService())
                .orElseThrow(() -> new IllegalStateException("Account Information Service not found"));
        dispatcher = Optional.ofNullable(context.getSystem().dispatchers().lookup("application.blocking-io-dispatcher"))
                .orElseThrow(() -> new IllegalStateException("blocking-io-dispatcher not found"));

        context.setApiService(this);


    }

    private void validateModel() {
        //TODO
    }


}
