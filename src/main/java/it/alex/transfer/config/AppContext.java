package it.alex.transfer.config;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import com.typesafe.config.Config;
import it.alex.transfer.service.AccountInfoService;
import it.alex.transfer.service.ApiService;
import it.alex.transfer.service.TransferService;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppContext {
    private ActorSystem system;
    private ActorMaterializer materializer;
    private Config applicationConfig;
    private TransferService transferService;
    private ApiService apiService;
    private AccountInfoService accountInfoService;

}
