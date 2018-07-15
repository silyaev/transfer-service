package it.alex.transfer.service;

import it.alex.transfer.config.AppContext;
import it.alex.transfer.model.Account;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.ExecutionContextExecutor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
public class ApiService {

    private final AppContext context;

    private TransferService transferService;
    private AccountInfoService accountInfoService;
    private ExecutionContextExecutor dispatcher;

    public static ApiService newInstance(final AppContext context) {
        final ApiService apiService = new ApiService(context);
        apiService.init();
        return apiService;
    }

    public CompletionStage<TransferResponse> moveMoney(TransferRequest request) {

        //TODO
        return null;
    }

    public CompletionStage<Optional<Account>> getAccount(Long id) {

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
