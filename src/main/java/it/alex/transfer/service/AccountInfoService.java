package it.alex.transfer.service;

import it.alex.transfer.model.AccountResponse;

import java.util.Optional;

public interface AccountInfoService {

    /**
     * Get information about account by account id.
     * The response includes current account balance.
     *
     * @param id Account id
     * @return Optional<AccountResponse>
     */
    Optional<AccountResponse> findAccount(Long id);
}
