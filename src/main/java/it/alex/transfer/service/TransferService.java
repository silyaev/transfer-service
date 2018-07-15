package it.alex.transfer.service;

import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;

public interface TransferService {

    TransferResponse moveMoney(TransferRequest request);
}
