package com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer;

import com.pilot.commons.Action;
import com.pilot.commons.Sms;
import com.pilot.commons.SmsRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("producer")
public class Producer {


//    @Value("${spring.cloud.stream.bindings.producer-out-0.destination}")
    @Value("${spring.kafka.template.default-topic}")
    private String smsTopic;

//    @Value("${spring.cloud.stream.bindings.producer-out-1.destination}")
    @Value("${spring.kafka.template.topic}")
    private String ruleTopic;

    @Value("${spring.kafka.template.partition-count}")
    private int partitionCount;

    @Value("${spring.kafka.template.replication-factor}")
    private short replicationFactor;

    @Bean
    public NewTopic initSmsTopic() {
        return new NewTopic(smsTopic, partitionCount, replicationFactor);
    }

    @Bean
    public NewTopic initRuleTopic() {
        return new NewTopic(ruleTopic, partitionCount, replicationFactor);
    }

    @Autowired
    private KafkaTemplate<Object, Object> template;

    public void send(Sms sms) {
        Action action = new Action(sms);
        log.info(String.format("#~#: Producing (key: " + sms.getReceiver() + "), action -> %s", action));
        this.template.send(smsTopic, sms.getReceiver(), action );
    }

    public void send(SmsRule smsRule) {
        Action action = new Action(smsRule);
        log.info(String.format("#~#: Producing (key: " + smsRule.getReceiver() + "), action -> %s", action));
        this.template.send(ruleTopic, smsRule.getReceiver(), action );
    }
}
