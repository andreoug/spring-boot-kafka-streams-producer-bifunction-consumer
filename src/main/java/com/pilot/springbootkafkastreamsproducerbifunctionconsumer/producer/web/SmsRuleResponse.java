package com.pilot.springbootkafkastreamsproducerbifunctionconsumer.producer.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by gandreou on 08/01/2022.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsRuleResponse {
    private Integer status;
    private String notes;
    String verb;
    Boolean allSenders;
    String receiver;
}
