package it.alex.transfer.exception;

import it.alex.transfer.model.TransferStatus;

public class TransferServiceExeption extends RuntimeException {

    final TransferStatus status;

    public TransferServiceExeption(String message, TransferStatus status) {
        super(message);
        this.status = status;
    }
}
