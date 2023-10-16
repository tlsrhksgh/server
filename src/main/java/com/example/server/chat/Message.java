package com.example.server.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String from;
    private String text;
}