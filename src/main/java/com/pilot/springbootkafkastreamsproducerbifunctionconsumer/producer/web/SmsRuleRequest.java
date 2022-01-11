package com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by gandreou on 08/01/2022.
 */
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SmsRuleRequest {
    String verb;
    Boolean allSenders;
    String receiver;
}
