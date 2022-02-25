package com.pilot.springbootkafkastreamsproducerbifunctionconsumer.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by gandreou on 29/12/2021.
 */
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequest {
    String body;
    String sender;
    String receiver;
}
