package it.alex.transfer.service;

import it.alex.transfer.model.AccountResponse;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ApiService {

     CompletionStage<TransferResponse> moveMoney(TransferRequest request) ;

     CompletionStage<Optional<AccountResponse>> getAccount(Long id) ;
}
