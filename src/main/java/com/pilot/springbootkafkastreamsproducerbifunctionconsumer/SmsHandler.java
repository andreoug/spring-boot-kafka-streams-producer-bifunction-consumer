package com.pilot.springbootkafkastreamsproducerbifunctionconsumer;

import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.service.MessageService;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web.SmsRequest;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web.SmsRuleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Created by gandreou on 26/02/2022.
 */
@Component
@Profile("producer")
public class SmsHandler {

    @Autowired
    MessageService messageService;

    public Mono<ServerResponse> sendSms(ServerRequest request){
        return request.bodyToMono(SmsRequest.class)
                .flatMap(smsRequest -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(messageService.send(smsRequest))));
    }

    public Mono<ServerResponse> sendSmsRule(ServerRequest request){
        return request.bodyToMono(SmsRuleRequest.class)
                .flatMap(smsRuleRequest -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(messageService.send(smsRuleRequest))));
    }
}
