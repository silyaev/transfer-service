package it.alex.transfer.repository;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import it.alex.transfer.config.PersistenceConfig;
import it.alex.transfer.entity.AccountBalanceEntity;
import it.alex.transfer.entity.AccountEntity;
import it.alex.transfer.entity.TransferHistoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(JUnit4.class)
@Slf4j
public class AccountRepositoryIntegrationTest {

    private static final String DB_DATA_LOG = "DB_DATA {}";
    private static SessionFactory sessionFactory;
    private static ActorSystem system;

    @BeforeClass
    public static void setUp() {
        system = ActorSystem.create("transfer");
        final Config applicationConfig = system.settings().config().getConfig("application");
        sessionFactory = PersistenceConfig.newInstance(applicationConfig).getSessionFactory();

    }

    @AfterClass()
    public static void close() {
        sessionFactory.close();
        system.terminate();
    }


    @Test
    public void readRecords_AccountEntity_Ok() {
        try (final Session session = sessionFactory.openSession()) {

            List<AccountEntity> data = session.createQuery("from AccountEntity",
                    AccountEntity.class).getResultList();

            log.info(DB_DATA_LOG, data);
            assertNotNull(data);
            assertFalse(data.isEmpty());
        }
    }

    @Test
    public void readRecords_AccountBalanceEntity_Ok() {
        try (final Session session = sessionFactory.openSession()) {

            List<AccountBalanceEntity> data = session.createQuery("from AccountBalanceEntity",
                    AccountBalanceEntity.class).getResultList();

            log.info(DB_DATA_LOG, data);
            assertNotNull(data);
            assertFalse(data.isEmpty());
        }
    }

    @Test
    public void readRecords_TransferHistoryEntity_Ok() {
        try (final Session session = sessionFactory.openSession()) {

            List<TransferHistoryEntity> data = session.createQuery("from TransferHistoryEntity",
                    TransferHistoryEntity.class).getResultList();

            log.info(DB_DATA_LOG, data);
            assertNotNull(data);
            assertFalse(data.isEmpty());
        }
    }

    @Test
    public void saveAccount_addNew_Ok() {

        AccountEntity account = AccountEntity.builder()
                .description("d")
                .name("name")
                .number("number")
                .build();

        AccountEntity result = null;

        try (final Session session = sessionFactory.openSession()) {

            AccountRepository repository = AccountRepository.newInstance(session);
            final Transaction transaction = session.getTransaction();

            transaction.begin();

            try {
                result = repository.saveAccount(account);
                transaction.commit();
            } catch (Exception ex) {

                transaction.rollback();
                throw ex;
            }
        }

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getCreationDateTime());
        assertNotNull(result.getUpdateDateTime());

        AccountBalanceEntity balance = null;
        try (final Session session = sessionFactory.openSession()) {
            balance = session.get(AccountBalanceEntity.class, result.getId());
        }

        assertNotNull(balance);
        assertNotNull(balance.getId());
        assertNotNull(balance.getLastUpdated());
        assertEquals(BigDecimal.ZERO, balance.getValue().setScale(0));
    }

    @Test
    public void saveAccount_update_Ok() {

        AccountEntity account = null;

        try (final Session session = sessionFactory.openSession()) {
            account = session.get(AccountEntity.class, 1L);
        }
        assertNotNull(account);
        account.setDescription("Test");

        AccountEntity result = null;
        try (final Session session = sessionFactory.openSession()) {

            AccountRepository repository = AccountRepository.newInstance(session);
            final Transaction transaction = session.getTransaction();

            transaction.begin();

            try {
                result = repository.saveAccount(account);
                transaction.commit();
            } catch (Exception ex) {
                transaction.rollback();
                throw ex;
            }
        }

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getCreationDateTime());
        assertNotNull(result.getUpdateDateTime());


        try (final Session session = sessionFactory.openSession()) {
            account = session.get(AccountEntity.class, 1L);
        }

        assertNotNull(account);
        assertNotNull(account.getId());
        assertEquals("Test",account.getDescription());
    }

    @Test
    public void findAccountById_ID_Ok() {

        AccountEntity account = null;

        try (final Session session = sessionFactory.openSession()) {
            account = AccountRepository.newInstance(session)
                    .findAccountById(1L)
                    .orElseThrow(() -> new IllegalStateException("account not found"));
        }
        assertNotNull(account);
        assertNotNull(account.getId());
        assertNotNull(account.getCreationDateTime());
        assertNotNull(account.getUpdateDateTime());
    }

    @Test
    public void findAccountById_ID_notFound() {

        try (final Session session = sessionFactory.openSession()) {
            Optional<AccountEntity> account = AccountRepository.newInstance(session).findAccountById(10L);
            assertFalse(account.isPresent());
        }
    }

    @Test
    public void findBalanceById_ID_Ok() {

        AccountBalanceEntity balance = null;

        try (final Session session = sessionFactory.openSession()) {
            balance = AccountRepository.newInstance(session)
                    .findBalanceById(2L)
                    .orElseThrow(() -> new IllegalStateException("account balance not found"));
        }
        assertNotNull(balance);
        assertNotNull(balance.getId());
        assertNotNull(balance.getLastUpdated());

    }

    @Test
    public void findBalanceById_ID_notFound() {

        try (final Session session = sessionFactory.openSession()) {
            Optional<AccountBalanceEntity> account = AccountRepository.newInstance(session).findBalanceById(10L);
            assertFalse(account.isPresent());
        }
    }

    @Test
    public void updateBalance_update_Ok() {

        AccountBalanceEntity balance = null;
        BigDecimal value = BigDecimal.valueOf(15.45);
        try (final Session session = sessionFactory.openSession()) {
            balance = session.get(AccountBalanceEntity.class, 1L);
        }
        assertNotNull(balance);
        balance.setValue(value);

        try (final Session session = sessionFactory.openSession()) {

            AccountRepository repository = AccountRepository.newInstance(session);
            final Transaction transaction = session.getTransaction();

            transaction.begin();

            try {
                repository.updateBalance(balance);
                transaction.commit();
            } catch (Exception ex) {
                transaction.rollback();
                throw ex;
            }
        }


        AccountBalanceEntity result = null;
        try (final Session session = sessionFactory.openSession()) {
            result = session.get(AccountBalanceEntity.class, 1L);
        }

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(value, result.getValue().setScale(2));
    }

}