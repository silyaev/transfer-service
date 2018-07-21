package it.alex.transfer.service;

import it.alex.transfer.model.AccountResponse;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ApiService {

     /**
      * Transfer money value from one account to other
      *
      * @param request TransferRequest
      * @return CompletionStage<TransferResponse>
      */
     CompletionStage<TransferResponse> moveMoney(TransferRequest request) ;

     /**
      * Get information about account by account id.
      * The response includes current account balance.
      *
      * @param id Account id
      * @return CompletionStage<Optional<AccountResponse>>
      */
     CompletionStage<Optional<AccountResponse>> getAccount(Long id) ;
}
