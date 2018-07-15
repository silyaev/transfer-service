package it.alex.transfer.service;

import it.alex.transfer.model.Account;

import java.util.Optional;

public interface AccountInfoService {
    Optional<Account> findAccount(Long is);
}
