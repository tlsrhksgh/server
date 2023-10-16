package com.example.server.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputMessage {
    private String from;
    private String text;
    private String time;
}