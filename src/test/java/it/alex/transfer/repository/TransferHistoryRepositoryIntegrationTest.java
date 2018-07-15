package it.alex.transfer.repository;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import it.alex.transfer.config.PersistenceConfig;
import it.alex.transfer.entity.TransferHistoryEntity;
import it.alex.transfer.model.TransferStatus;
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

import static org.junit.Assert.*;


@RunWith(JUnit4.class)
@Slf4j
public class TransferHistoryRepositoryIntegrationTest {

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
    public void findHistoryById_id_Ok() {

        TransferHistoryEntity balance = null;

        try (final Session session = sessionFactory.openSession()) {
            balance = TransferHistoryRepository.newInstance(session)
                    .findHistoryById(1L)
                    .orElseThrow(() -> new IllegalStateException("Transfer History  balance not found"));
        }
        assertNotNull(balance);
        assertNotNull(balance.getId());
        assertNotNull(balance.getValue());
        assertNotNull(balance.getStatus());

    }

    @Test
    public void findHistoryBySourceId_id_Ok() {

        List<TransferHistoryEntity> balances = null;

        try (final Session session = sessionFactory.openSession()) {
            balances = TransferHistoryRepository.newInstance(session)
                    .findHistoryBySourceId(1L);
        }
        assertNotNull(balances);
        assertFalse(balances.isEmpty());
    }

    @Test
    public void findHistoryByTargetId_id_Ok() {

        List<TransferHistoryEntity> balances = null;

        try (final Session session = sessionFactory.openSession()) {
            balances = TransferHistoryRepository.newInstance(session)
                    .findHistoryByTargetId(1L);
        }
        assertNotNull(balances);
        assertFalse(balances.isEmpty());
    }

    @Test
    public void save_addNew_Ok() {

        TransferHistoryEntity historyEntity = TransferHistoryEntity.builder()
                .description("start")
                .status(TransferStatus.PENDING)
                .value(BigDecimal.valueOf(12.21))
                .fromAccount(1)
                .toAccount(2)
                .build();

        TransferHistoryEntity result = null;

        try (final Session session = sessionFactory.openSession()) {

            TransferHistoryRepository repository = TransferHistoryRepository.newInstance(session);
            final Transaction transaction = session.getTransaction();

            transaction.begin();

            try {
                result = repository.save(historyEntity);
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

    }


    @Test
    public void save_update_Ok() {

        TransferHistoryEntity record = null;

        try (final Session session = sessionFactory.openSession()) {
            record = session.get(TransferHistoryEntity.class, 1L);
        }
        assertNotNull(record);
        record.setDescription("Test");

        TransferHistoryEntity result = null;
        try (final Session session = sessionFactory.openSession()) {

            TransferHistoryRepository repository = TransferHistoryRepository.newInstance(session);
            final Transaction transaction = session.getTransaction();

            transaction.begin();

            try {
                result = repository.save(record);
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
            record = session.get(TransferHistoryEntity.class, 1L);
        }

        assertNotNull(record);
        assertNotNull(record.getId());
        assertEquals("Test", record.getDescription());
    }
}