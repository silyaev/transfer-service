package it.alex.transfer.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Getter
public class HttpConfig {
    private static final String PARAMETER_HOST = "host";
    private static final String PARAMETER_PORT = "port";
    private static final String PARAMETER_HTTP = "http";

    private final Config applicationConfig;
    private Config config;
    private String host;
    private Integer port;

    public static HttpConfig newInstance(final Config applicationConfig) {
        final HttpConfig config = new HttpConfig(applicationConfig);
        config.init();
        return config;
    }

    private void init() {
        config = Optional.ofNullable(applicationConfig.getConfig(PARAMETER_HTTP))
                .orElseThrow(() -> new IllegalArgumentException("Http config not found in application config"));
        log.info("Start prepared Http Server config for {}", config);

        host = Optional.ofNullable(config.getString(PARAMETER_HOST))
                .orElseThrow(() -> new IllegalArgumentException("host value not found"));
        port = Integer.valueOf(Optional.ofNullable(config.getString(PARAMETER_PORT))
                .orElseThrow(() -> new IllegalArgumentException("host value not found")));
    }

}
