package it.alex.transfer;

import akka.actor.ActorSystem;
import akka.http.javadsl.server.Route;
import com.typesafe.config.Config;
import it.alex.transfer.config.AppContext;
import it.alex.transfer.config.HttpConfig;
import it.alex.transfer.config.PersistenceConfig;
import it.alex.transfer.http.HttpRouter;
import it.alex.transfer.http.HttpServer;
import it.alex.transfer.service.AccountInfoService;
import it.alex.transfer.service.TransferService;
import it.alex.transfer.service.impl.AccountInfoServiceImpl;
import it.alex.transfer.service.impl.ApiServiceImpl;
import it.alex.transfer.service.impl.TransferServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TransferApp {

    private static final int OK_CODE = 0;
    private static final int ERROR_CODE = -1;
    private ActorSystem system;

    public static void main(String[] args) {
        System.exit(new TransferApp().run(args));
    }

    public int run(String[] args) {
        log.info("Simple money transfer service is starting and use command line arguments {}", (Object) args);

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Close by ShutdownHook.");
                stop();
            }));
            start();

        } catch (Exception ex) {
            log.error("Application error ", ex);
            return ERROR_CODE;
        }

        return OK_CODE;
    }

    public void start() throws ExecutionException, InterruptedException {

        system = ActorSystem.create("routes");

        final Config applicationConfig = system.settings().config().getConfig("application");
        final HttpConfig httpConfig = HttpConfig.newInstance(applicationConfig);
        final SessionFactory sessionFactory = PersistenceConfig.newInstance(applicationConfig).getSessionFactory();
        final AccountInfoService accountInfoService = AccountInfoServiceImpl.newInstance(sessionFactory);
        final TransferService transferService = TransferServiceImpl.newInstance(sessionFactory);

        final AppContext context = AppContext.builder()
                .applicationConfig(applicationConfig)
                .system(system)
                .accountInfoService(accountInfoService)
                .transferService(transferService)
                .build();

        ApiServiceImpl.newInstance(context);

        final Route route = HttpRouter.newInstance(context).createRoute();
        final HttpServer httpServer = HttpServer.newInstance(context, httpConfig, route);
        httpServer.start();

    }

    public void stop() {
        log.info("Start application shutdown");
        system.terminate();
        log.info("System is stopped");
    }


}
