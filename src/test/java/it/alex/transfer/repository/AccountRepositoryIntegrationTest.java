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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
            assertEquals(3, data.size());
        }
    }

    @Test
    public void readRecords_AccountBalanceEntity_Ok() {
        try (final Session session = sessionFactory.openSession()) {

            List<AccountBalanceEntity> data = session.createQuery("from AccountBalanceEntity",
                    AccountBalanceEntity.class).getResultList();

            log.info(DB_DATA_LOG, data);
            assertNotNull(data);
            assertEquals(3, data.size());
        }
    }

    @Test
    public void readRecords_TransferHistoryEntity_Ok() {
        try (final Session session = sessionFactory.openSession()) {

            List<TransferHistoryEntity> data = session.createQuery("from TransferHistoryEntity",
                    TransferHistoryEntity.class).getResultList();

            log.info(DB_DATA_LOG, data);
            assertNotNull(data);
            assertEquals(4, data.size());
        }
    }
}