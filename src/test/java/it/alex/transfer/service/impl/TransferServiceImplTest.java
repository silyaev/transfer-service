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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    @Captor
    private ArgumentCaptor<AccountBalanceEntity> balanceCaptor;


    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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

        verify(transaction, times(2)).commit();
        verify(transaction, never()).rollback();
        verify(accountRepository, times(2)).updateBalance(balanceCaptor.capture());

        List<AccountBalanceEntity> values = balanceCaptor.getAllValues();

        assertNotNull(values);
        assertEquals(2, values.size());

        assertEquals(BigDecimal.valueOf(76.55), values.get(0).getValue());
        assertEquals(BigDecimal.valueOf(44.04), values.get(1).getValue());

        assertNotNull(result);
        assertNotNull(result.getReferenceId());
        assertEquals(TransferStatus.COMPLETED, result.getStatus());
        assertEquals(BigDecimal.valueOf(76.55), result.getBalance());
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

        verify(transaction, times(2)).commit();
        verify(transaction, never()).rollback();
        verify(accountRepository, never()).updateBalance(balanceCaptor.capture());


        assertNotNull(result);
        assertNotNull(result.getReferenceId());
        assertEquals(TransferStatus.DECLINED, result.getStatus());
        assertEquals(BigDecimal.valueOf(100), result.getBalance());
        assertEquals(request, result.getRequest());

    }


    @Test
    public void moveMoney_valid_DataValuesValidationException() {

        TransferRequest request = TransferRequest.builder()
                .description("test validation")
                .fromAccountId(1L)
                .toAccountId(2L)
                .value(BigDecimal.valueOf(123.45))
                .build();

        when(historyRepository.save(any()))
                .thenReturn(getHistoryResult(request));
        when(historyRepository.findHistoryById(any()))
                .thenReturn(Optional.empty());

        when(accountRepository.findBalanceById(request.getFromAccountId()))
                .thenReturn(getSourceBalance(request));

        when(accountRepository.findBalanceById(request.getToAccountId()))
                .thenReturn(getTargetBalance(request));

        exceptionRule.expect(DataValuesValidationException.class);
        exceptionRule.expectMessage("History record not found for recordId=10");

        service.moveMoney(request);

    }

    @Test
    public void moveMoney_valid_transactionDataValuesValidationException() {

        TransferRequest request = TransferRequest.builder()
                .description("test validation")
                .fromAccountId(1L)
                .toAccountId(2L)
                .value(BigDecimal.valueOf(123.45))
                .build();

        when(historyRepository.save(any()))
                .thenReturn(getHistoryResult(request));
        when(historyRepository.findHistoryById(any()))
                .thenReturn(Optional.empty());

        when(accountRepository.findBalanceById(request.getFromAccountId()))
                .thenReturn(getSourceBalance(request));

        when(accountRepository.findBalanceById(request.getToAccountId()))
                .thenReturn(getTargetBalance(request));

        try {
            service.moveMoney(request);
        } catch (DataValuesValidationException ex) {
            verify(transaction).commit();
            verify(transaction).rollback();
            verify(accountRepository, never()).updateBalance(balanceCaptor.capture());
        }
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