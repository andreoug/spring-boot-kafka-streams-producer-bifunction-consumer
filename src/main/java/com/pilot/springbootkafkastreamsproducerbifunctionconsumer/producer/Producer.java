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
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
@Profile("producer")
public class Producer {

    @Value("${spring.kafka.template.default-topic}")
    private String smsTopic;

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

    public void send(String topic, String key, Action action) {
        log.info("#~#: Producing (key: {}) action -> {}}", key, action);
        ListenableFuture<SendResult<Object, Object>> future = this.template.send(topic, key, action);
        future.addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("#~#: Unable to send key={} with action=[ {} ] due to : {}", key, action, ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                log.info("#~#: Sent key={} with action=[ {} ] on offset=[ {} ]", key, action, result.getRecordMetadata().offset());
            }
        });
    }

    public void send(Sms sms) {
        this.send(smsTopic, sms.getReceiver(), new Action(sms));
    }

    public void send(SmsRule smsRule) {
        this.send(ruleTopic, smsRule.getReceiver(), new Action(smsRule));
    }
}