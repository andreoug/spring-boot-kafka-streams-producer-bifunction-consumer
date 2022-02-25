package com.pilot.springbootkafkastreamsproducerbifunctionconsumer;

import com.pilot.commons.Action;
import com.pilot.commons.Sms;
import com.pilot.commons.SmsRule;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.web.SmsRequest;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.web.SmsResponse;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.web.SmsRuleRequest;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.web.SmsRuleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.function.Function;

@Slf4j
@Configuration
@Profile("producer")
public class KafkaProducerConfiguration {

    @Value("${spring.cloud.stream.bindings.sendSms-out-0.destination}")
    private String smsTopic;

    @Value("${spring.cloud.stream.bindings.sendSms-out-0.producer.partition-count}")
    private int smsTopicPartitionCount;

    @Value("${spring.cloud.stream.bindings.sendSms-out-0.producer.replication-factor}")
    private short smsTopicReplicationFactor;

    @Value("${spring.cloud.stream.bindings.sendRule-out-0.destination}")
    private String ruleTopic;

    @Value("${spring.cloud.stream.bindings.sendRule-out-0.producer.partition-count}")
    private int ruleTopicPartitionCount;

    @Value("${spring.cloud.stream.bindings.sendRule-out-0.producer.replication-factor}")
    private short ruleTopicReplicationFactor;

    @Bean
    public NewTopic initSmsTopic() {
        return new NewTopic(smsTopic, smsTopicPartitionCount, smsTopicReplicationFactor);
    }

    @Bean
    public NewTopic initRuleTopic() {
        return new NewTopic(ruleTopic, ruleTopicPartitionCount, ruleTopicReplicationFactor);
    }

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @Bean
    public Function<SmsRequest, SmsResponse> sendSms() {
        return request -> {
            Sms sms = new Sms(request.getBody());
            sms.setSender(request.getSender());
            sms.setReceiver(request.getReceiver());
            Action action = new Action(sms);
            log.info(String.format("#~#: Producing (key: %s), action -> %s", sms.getReceiver(), action));
            template.send(smsTopic, sms.getReceiver(), action );
            log.info(String.format("#~#: Produced (key: %s), ", action.getId()));
            return new SmsResponse(200,null,
                    request.getSender(), request.getReceiver());
        };
  }

    @Bean
    public Function<SmsRuleRequest, SmsRuleResponse> sendRule() {
        return request -> {
            SmsRule smsRule = new SmsRule(request.getVerb(),request.getAllSenders(),request.getReceiver());
            Action action = new Action(smsRule);
            log.info(String.format("#~#: Producing (key: %s), action -> %s", smsRule.getReceiver(), action));
            template.send(ruleTopic, smsRule.getReceiver(), action );
            log.info(String.format("#~#: Produced (key: %s), ", action.getId()));
            return new SmsRuleResponse(200,null, request.getVerb(),
                    request.getAllSenders(), request.getReceiver());
        };
    }
}
