package it.alex.transfer.service;

import it.alex.transfer.exception.DataValuesValidationException;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;

public interface TransferService {

    /**
     * Transfer money value from one account to other account
     *
     * @param request TransferRequest for make operation
     * @return Transfer Response
     * @throws DataValuesValidationException
     */
    TransferResponse moveMoney(TransferRequest request);
}
