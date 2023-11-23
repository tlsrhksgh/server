package com.example.server.member.client.mailgun;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SendMailForm {
    private String to;
    private String from;
    private String subject;
    private String text;
}
