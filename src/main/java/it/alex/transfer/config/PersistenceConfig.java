package it.alex.transfer.config;

import com.typesafe.config.Config;

import it.alex.transfer.entity.AccountEntity;
import it.alex.transfer.entity.TransferHistoryEntity;
import it.alex.transfer.entity.AccountBalanceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class PersistenceConfig {

    private static final String DATA_BASE = "data-base";
    private static final String DRIVER = "driver";
    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASS = "pass";
    private static final String DIALECT = "dialect";
    private static final String VALIDATE = "validate";
    private static final String HIBERNATE_HIKARI_CONNECTION_TIMEOUT = "hibernate.hikari.connectionTimeout";
    private static final String HIBERNATE_HIKARI_MINIMUM_IDLE = "hibernate.hikari.minimumIdle";
    private static final String HIBERNATE_HIKARI_MAXIMUM_POOL_SIZE = "hibernate.hikari.maximumPoolSize";
    private static final String HIBERNATE_HIKARI_IDLE_TIMEOUT = "hibernate.hikari.idleTimeout";
    private static final String HIBERNATE_HIKARI_AUTO_COMMIT = "hibernate.hikari.autoCommit";
    private static final String JDBC = "jdbc";
    private static final String HIKARI = "hikari";
    private static final String CONNECTION_TIMEOUT = "connection-timeout";
    private static final String MINIMUM_IDLE = "minimum-idle";
    private static final String MAXIMUM_POOL_SIZE = "maximum-pool-size";
    private static final String IDLE_TIMEOUT = "idle-timeout";
    private static final String AUTO_COMMIT = "auto-commit";

    private final Config applicationConfig;

    private SessionFactory sessionFactory;

    public static PersistenceConfig newInstance(final Config applicationConfig) {
        return new PersistenceConfig(applicationConfig);

    }

    public synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            init();
        }
        return sessionFactory;
    }

    private PersistenceConfig init() {

        final Config dbConfig = Optional.of(applicationConfig.getConfig(DATA_BASE))
                .orElseThrow(() -> new IllegalArgumentException("dbConfig in section " + DATA_BASE + "  not found"));

        doBaseMigrate(dbConfig);

        final Map<String, Object> settings = new HashMap<>();
        settings.put(Environment.DRIVER, dbConfig.getString(DRIVER));
        settings.put(Environment.URL, dbConfig.getString(URL));
        settings.put(Environment.USER, dbConfig.getString(USER));
        settings.put(Environment.PASS, dbConfig.getString(PASS));
        settings.put(Environment.DIALECT, dbConfig.getString(DIALECT));
        settings.put(Environment.HBM2DDL_AUTO, VALIDATE);
        settings.put(Environment.SHOW_SQL, true);


        final Config hikariConfig = Optional.of(dbConfig.getConfig(HIKARI))
                .orElseThrow(() -> new IllegalArgumentException("hikariConfig in section " + HIKARI + "  not found"));

        settings.put(HIBERNATE_HIKARI_CONNECTION_TIMEOUT, hikariConfig.getString(CONNECTION_TIMEOUT));
        settings.put(HIBERNATE_HIKARI_MINIMUM_IDLE, hikariConfig.getString(MINIMUM_IDLE));
        settings.put(HIBERNATE_HIKARI_MAXIMUM_POOL_SIZE, hikariConfig.getString(MAXIMUM_POOL_SIZE));
        settings.put(HIBERNATE_HIKARI_IDLE_TIMEOUT, hikariConfig.getString(IDLE_TIMEOUT));
        settings.put(HIBERNATE_HIKARI_AUTO_COMMIT, hikariConfig.getString(AUTO_COMMIT));


        final ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .applySetting(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, JDBC)
                .build();

        Metadata metadata = new MetadataSources(standardRegistry)
                .addAnnotatedClass(AccountEntity.class)
                .addAnnotatedClass(AccountBalanceEntity.class)
                .addAnnotatedClass(TransferHistoryEntity.class)
                .getMetadataBuilder()
                .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
                .build();

        sessionFactory = metadata.getSessionFactoryBuilder().build();

        return this;
    }

    private void doBaseMigrate(final Config dbConfig) {
        final Flyway flyway = new Flyway();

        flyway.setDataSource(dbConfig.getString(URL), dbConfig.getString(USER), dbConfig.getString(PASS));
        flyway.migrate();
    }
}
