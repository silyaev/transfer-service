package it.alex.transfer.service;

import it.alex.transfer.model.AccountResponse;

import java.util.Optional;

public interface AccountInfoService {
    Optional<AccountResponse> findAccount(Long is);
}
