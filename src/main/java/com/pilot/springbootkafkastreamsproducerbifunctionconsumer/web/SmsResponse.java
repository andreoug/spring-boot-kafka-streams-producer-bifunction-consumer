package com.pilot.springbootkafkastreamsproducerbifunctionconsumer.web;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Created by gandreou on 29/12/2021.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponse {
    private Integer status;
    private String notes;
    String sender;
    String receiver;
}
