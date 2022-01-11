package com.pilot.commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by gandreou on 14/11/2021.
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sms {
    String body;
    String sender;
    String receiver;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public Sms(String body) {
        this.body = body;
        this.timestamp = LocalDateTime.now();
    }
}
