package it.alex.transfer.service.impl;

import it.alex.transfer.config.AppContext;
import it.alex.transfer.exception.RequestValidationException;
import it.alex.transfer.model.AccountResponse;
import it.alex.transfer.model.ErrorResponse;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;
import it.alex.transfer.service.AccountInfoService;
import it.alex.transfer.service.ApiService;
import it.alex.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.ExecutionContextExecutor;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

    private final AppContext context;

    private TransferService transferService;
    private AccountInfoService accountInfoService;
    private ExecutionContextExecutor dispatcher;
    private Validator validator;

    public static ApiServiceImpl newInstance(final AppContext context) {
        final ApiServiceImpl apiService = new ApiServiceImpl(context);
        apiService.init();
        return apiService;
    }

    public CompletionStage<TransferResponse> moveMoney(TransferRequest request) {
        validateModel(request);
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

        validator = Validation.buildDefaultValidatorFactory().getValidator();

        context.setApiService(this);


    }

    private void validateModel(TransferRequest request) {
        final List<String> errors = validator.validate(request).stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {

            throw new RequestValidationException("Validation error " + errors, ErrorResponse.builder()
                    .errors(errors)
                    .description("Validation error")
                    .code(0)
                    .request(request.getClass().getSimpleName())
                    .build());
        }

    }


}
