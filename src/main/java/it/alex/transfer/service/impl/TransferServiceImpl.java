package it.alex.transfer.service.impl;


import it.alex.transfer.entity.AccountBalanceEntity;
import it.alex.transfer.entity.TransferHistoryEntity;
import it.alex.transfer.exception.DataValuesValidationException;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;
import it.alex.transfer.model.TransferStatus;
import it.alex.transfer.repository.AccountRepository;
import it.alex.transfer.repository.TransferHistoryRepository;
import it.alex.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final SessionFactory sessionFactory;

    public static TransferService newInstance(SessionFactory sessionFactory) {
        return new TransferServiceImpl(sessionFactory);
    }


    @Override
    public TransferResponse moveMoney(final TransferRequest request) {

        final TransferResponse.TransferResponseBuilder responseBuilder = TransferResponse.builder()
                .id(UUID.randomUUID().toString())
                .request(request);

        try (final Session session = sessionFactory.openSession()) {

            Long recordId = saveToHistory(request, session);
            log.debug("created history record recordId={} for {}", recordId, request);
            responseBuilder.referenceId(recordId);

            makeOperation(request, recordId, responseBuilder, session);

        }

        return responseBuilder.build();
    }

    private void makeOperation(TransferRequest request, Long recordId,
                               TransferResponse.TransferResponseBuilder responseBuilder, Session session) {

        final AccountRepository accountRepository = AccountRepository.newInstance(session);
        final TransferHistoryRepository historyRepository = TransferHistoryRepository.newInstance(session);

        final Transaction transaction = session.getTransaction();

        transaction.begin();
        try {

            makeInTransaction(request, recordId, responseBuilder, accountRepository, historyRepository);

            transaction.commit();
        } catch (Exception ex) {
            log.error("Try make operation ", ex);
            transaction.rollback();
            throw ex;
        }

    }

    private void makeInTransaction(TransferRequest request, Long recordId,
                                   TransferResponse.TransferResponseBuilder responseBuilder,
                                   AccountRepository accountRepository, TransferHistoryRepository historyRepository) {

        AccountBalanceEntity sourceBalance = accountRepository.findBalanceById(request.getFromAccountId())
                .orElseThrow(() -> createDataError("Account balance not found for accountId "
                        + request.getFromAccountId()));

        BigDecimal sourceValue = Optional.ofNullable(sourceBalance.getValue())
                .orElseThrow(() -> createDataError("Account balance value is null for account "
                        + request.getToAccountId()));


        TransferHistoryEntity history = historyRepository.findHistoryById(recordId)
                .orElseThrow(() -> createDataError("History record not found for recordId="
                        + recordId));


        if (sourceValue.compareTo(request.getValue()) < 0) {
            history.setStatus(TransferStatus.DECLINED);
            historyRepository.save(history);
            responseBuilder.status(TransferStatus.DECLINED)
                    .balance(sourceValue)
                    .description("Don't have money on account for make operation. value=" + sourceValue);
        } else {
            AccountBalanceEntity targetBalance = accountRepository.findBalanceById(request.getToAccountId())
                    .orElseThrow(() -> createDataError("Target account balance not found for accountId "
                            + request.getToAccountId()));
            BigDecimal targetValue = Optional.ofNullable(targetBalance.getValue())
                    .orElseThrow(() -> createDataError("Target account balance value is null for account "
                            + request.getToAccountId()));

            BigDecimal newSourceValue = sourceValue.subtract(request.getValue());
            BigDecimal newTargetValue = targetValue.add(request.getValue());

            sourceBalance.setValue(newSourceValue);
            targetBalance.setValue(newTargetValue);
            accountRepository.updateBalance(sourceBalance);
            accountRepository.updateBalance(targetBalance);

            history.setStatus(TransferStatus.COMPLETED);
            historyRepository.save(history);

            responseBuilder.status(TransferStatus.COMPLETED)
                    .balance(newSourceValue)
                    .description("Done and new balance=" + newSourceValue);

        }
    }

    private Long saveToHistory(TransferRequest request, Session session) {


        TransferHistoryEntity historyEntity = TransferHistoryEntity.builder()
                .description(request.getDescription())
                .status(TransferStatus.PENDING)
                .value(request.getValue())
                .fromAccount(request.getFromAccountId())
                .toAccount(request.getToAccountId())
                .build();

        TransferHistoryRepository repository = TransferHistoryRepository.newInstance(session);
        final Transaction transaction = session.getTransaction();

        transaction.begin();
        TransferHistoryEntity result;
        try {
            result = repository.save(historyEntity);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw ex;
        }
        return Optional.ofNullable(result)
                .orElseThrow(() -> createDataError("History record is null"))
                .getId();
    }

    private RuntimeException createDataError(String message) {
        return new DataValuesValidationException(message);
    }
}
