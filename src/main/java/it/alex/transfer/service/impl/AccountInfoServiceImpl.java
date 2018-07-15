package it.alex.transfer.service.impl;

import it.alex.transfer.entity.AccountBalanceEntity;
import it.alex.transfer.entity.AccountEntity;
import it.alex.transfer.exception.DataValuesValidationException;
import it.alex.transfer.model.Account;
import it.alex.transfer.repository.AccountRepository;
import it.alex.transfer.service.AccountInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AccountInfoServiceImpl implements AccountInfoService {
    private final SessionFactory sessionFactory;

    public static AccountInfoService newInstance(SessionFactory sessionFactory) {
        return new AccountInfoServiceImpl(sessionFactory);
    }

    @Override
    public Optional<Account> findAccount(Long id) {

        Account.AccountBuilder builder = Account.builder()
                .id(id);
        try (final Session session = sessionFactory.openSession()) {

            final AccountRepository repository = AccountRepository.newInstance(session);

            final AccountEntity account = repository.findAccountById(id)
                    .orElseThrow(() -> new DataValuesValidationException("Account not found "));

            builder.number(account.getNumber())
                    .name(account.getName())
                    .description(account.getDescription());
            final AccountBalanceEntity balance = repository.findBalanceById(id)
                    .orElseThrow(() -> new DataValuesValidationException("Account balance not found for"));

            builder.balance(balance.getValue());

        } catch (DataValuesValidationException ex) {
            log.info("result findAccount id={} " + ex.getMessage());
            return Optional.empty();
        }

        return Optional.of(builder.build());
    }
}
