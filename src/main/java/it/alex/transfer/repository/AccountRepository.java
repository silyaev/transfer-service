package it.alex.transfer.repository;

import it.alex.transfer.entity.AccountBalanceEntity;
import it.alex.transfer.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AccountRepository {

    private final Session session;

    public static AccountRepository newInstance(Session session) {
        return new AccountRepository(session);
    }

    public AccountEntity saveAccount(AccountEntity account) {
        Long accountId = account.getId();

        if (accountId == null) {
            final Long id = (Long) session.save(account);
            account.setId(id);
            session.save(AccountBalanceEntity.builder()
                    .id(id)
                    .value(BigDecimal.ZERO)
                    .build());

        } else {
            session.update(account);
        }
        return account;
    }

    public Optional<AccountEntity> findAccountById(Long id) {
        return Optional.ofNullable(session.get(AccountEntity.class, id));
    }

    public Optional<AccountBalanceEntity> findBalanceById(Long id) {
        return Optional.ofNullable(session.get(AccountBalanceEntity.class, id));
    }

    public void updateBalance(AccountBalanceEntity balance) {
        session.update(balance);
    }

}
