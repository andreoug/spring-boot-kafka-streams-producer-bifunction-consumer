package com.pilot.springbootkafkastreamsproducerbifunctionconsumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Created by gandreou on 25/02/2022.
 */
@Configuration(proxyBeanMethods = false)
@Profile("producer")
public class SmsRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> routes(SmsHandler smsHandler) {
        return RouterFunctions.route()
                .POST("/kafka/send-sms", smsHandler::sendSms)
                .POST("/kafka/send-rule", smsHandler::sendSmsRule)
                .build();
    }

}
