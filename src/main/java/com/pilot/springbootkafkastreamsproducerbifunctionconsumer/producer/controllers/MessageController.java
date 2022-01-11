package com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.controllers;

import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.service.MessageService;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web.SmsRequest;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web.SmsResponse;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web.SmsRuleRequest;
import com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web.SmsRuleResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/kafka")
@Profile("producer")
public class MessageController {

    final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send-sms")
    public ResponseEntity<SmsResponse> sendSms(@RequestBody SmsRequest request) {
        return new ResponseEntity<>(messageService.send(request), HttpStatus.OK);
    }

    @PostMapping("/send-rule")
    public ResponseEntity<SmsRuleResponse> sendSmsRule(@RequestBody SmsRuleRequest request) {
        return new ResponseEntity<>(messageService.send(request), HttpStatus.OK);
    }

}
