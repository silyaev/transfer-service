package it.alex.transfer.service.impl;

import it.alex.transfer.entity.AccountBalanceEntity;
import it.alex.transfer.entity.TransferHistoryEntity;
import it.alex.transfer.model.TransferRequest;
import it.alex.transfer.model.TransferResponse;
import it.alex.transfer.model.TransferStatus;
import it.alex.transfer.repository.AccountRepository;
import it.alex.transfer.repository.TransferHistoryRepository;
import it.alex.transfer.service.TransferService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@PrepareForTest({AccountRepository.class, TransferHistoryRepository.class})
@RunWith(PowerMockRunner.class)
public class TransferServiceImplTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private TransferHistoryRepository historyRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Transaction transaction;


    private TransferService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(AccountRepository.class);
        PowerMockito.mockStatic(TransferHistoryRepository.class);

        when(AccountRepository.newInstance(any())).thenReturn(accountRepository);
        when(TransferHistoryRepository.newInstance(any())).thenReturn(historyRepository);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(transaction);

        service = TransferServiceImpl.newInstance(sessionFactory);

    }

    @Test
    public void moveMoney_valid_Ok() {

        TransferRequest request = TransferRequest.builder()
                .description("test")
                .fromAccountId(1L)
                .toAccountId(2L)
                .value(BigDecimal.valueOf(23.45))
                .build();

        when(historyRepository.save(any()))
                .thenReturn(getHistoryResult(request));
        when(historyRepository.findHistoryById(any()))
                .thenReturn(Optional.ofNullable(getHistoryResult(request)));

        when(accountRepository.findBalanceById(request.getFromAccountId()))
                .thenReturn(getSourceBalance(request));

        when(accountRepository.findBalanceById(request.getToAccountId()))
                .thenReturn(getTargetBalance(request));

        TransferResponse result = service.moveMoney(request);

        assertNotNull(result);
        assertNotNull(result.getReferenceId());
        assertEquals(TransferStatus.COMPLETED, result.getStatus());
        assertEquals(request, result.getRequest());

    }

    @Test
    public void moveMoney_valid_Decline() {

        TransferRequest request = TransferRequest.builder()
                .description("test")
                .fromAccountId(1L)
                .toAccountId(2L)
                .value(BigDecimal.valueOf(123.45))
                .build();

        when(historyRepository.save(any()))
                .thenReturn(getHistoryResult(request));
        when(historyRepository.findHistoryById(any()))
                .thenReturn(Optional.ofNullable(getHistoryResult(request)));

        when(accountRepository.findBalanceById(request.getFromAccountId()))
                .thenReturn(getSourceBalance(request));

        when(accountRepository.findBalanceById(request.getToAccountId()))
                .thenReturn(getTargetBalance(request));

        TransferResponse result = service.moveMoney(request);

        assertNotNull(result);
        assertNotNull(result.getReferenceId());
        assertEquals(TransferStatus.DECLINED, result.getStatus());
        assertEquals(request, result.getRequest());

    }

    private Optional<AccountBalanceEntity> getTargetBalance(TransferRequest request) {
        return Optional.of(AccountBalanceEntity.builder()
                .value(BigDecimal.valueOf(20.59))
                .id(request.getToAccountId())
                .build());
    }

    private Optional<AccountBalanceEntity> getSourceBalance(TransferRequest request) {

        return Optional.of(AccountBalanceEntity.builder()
                        .value(BigDecimal.valueOf(100))
                        .id(request.getFromAccountId())
                        .build());
    }

    private TransferHistoryEntity getHistoryResult(TransferRequest request) {

        TransferHistoryEntity result = TransferHistoryEntity.builder()
                .toAccount(request.getToAccountId())
                .fromAccount(request.getFromAccountId())
                .description(request.getDescription())
                .value(request.getValue())
                .build();

        result.setId(10L);
        return result;
    }
}