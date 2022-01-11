package com.pilot.springbootkafkastreamsproducerbifunctionconsumer;

import com.pilot.commons.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Consumer;

@Slf4j
@Configuration
@Profile("consumer")
public class KafkaConsumerConfiguration {

  @Bean
  public Consumer<KStream<String, Action>> consumeService() {
    return kstream -> kstream.foreach((key, action) ->
      log.info(String.format("Action consumed [%s] action.sms.body: [%s]", action, action.getSms().getBody()))
    );
  }
}
