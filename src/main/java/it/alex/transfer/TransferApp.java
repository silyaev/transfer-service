package it.alex.transfer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferApp {

    public static void main(String[] args) {
        System.exit(new TransferApp().run(args));
    }

    private int run(String[] args) {
        log.info("Simple money transfer service is starting and use command line arguments {}", (Object) args);
        return 0;
    }

}
