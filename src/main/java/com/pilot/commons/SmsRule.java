package com.pilot.commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by gandreou on 08/01/2022.
 */

@Data
@Getter
@NoArgsConstructor
public class SmsRule {
    String verb;
    Boolean allSenders;
    String receiver;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public SmsRule(String verb, Boolean allSenders, String receiver) {
        this.verb = verb;
        this.allSenders = allSenders;
        this.receiver = receiver;
        this.timestamp = LocalDateTime.now();
    }
}
