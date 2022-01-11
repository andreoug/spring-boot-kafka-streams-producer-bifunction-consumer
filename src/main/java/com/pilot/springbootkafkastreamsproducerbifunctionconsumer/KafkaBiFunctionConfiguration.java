package com.pilot.springbootkafkastreamsproducerbifunctionconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilot.commons.Action;
import com.pilot.commons.Status;
import com.pilot.commons.Verb;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.function.BiFunction;

@Slf4j
@Configuration
@Profile("bifunction")
public class KafkaBiFunctionConfiguration {

  @Bean
  public BiFunction<KStream<String, Action>, KTable<String, Action>,
          KStream<String, Action>> bifunctionProcessor() {

      ObjectMapper mapper = new ObjectMapper();
      Serde<Action> actionSerde = new JsonSerde<>( Action.class, mapper );

      return (actionsSmsStream, actionsRuleTable) -> actionsSmsStream
                  .leftJoin(actionsRuleTable,
                      (actionSms, actionRule) -> {
                            actionSms = actionSms.updateStatus(Status.PENDING.toString());
                            if( actionRule != null) {
                                actionSms.setSmsRule(actionRule.getSmsRule());
                            }
                            return actionSms;
                      },
                      Joined.with(Serdes.String(), actionSerde, null))
              .peek((key, actionSms) -> log.info("key: " + key + ", action: " + actionSms))
              .filter( (key,action) -> action.getSmsRule() == null ||
                      //ToDo-geand: add particular senders in the following versions while extending smsRules
                      action.getSmsRule().getVerb().equals(Verb.BLOCK.toString()) &&
                      action.getSmsRule().getAllSenders().equals(Boolean.TRUE) &&
                      //ToDo-geand: verify if there is any corner case for this last condition
                      !action.getSmsRule().getReceiver().equals(action.getSms().getReceiver())
              )
              .map( (key,action) -> KeyValue.pair(key, action.updateStatus(Status.DELIVERING.toString())));
  }
}
