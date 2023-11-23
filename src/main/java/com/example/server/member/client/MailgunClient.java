package com.example.server.member.client;

import com.example.server.member.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier("mailgun")
public interface MailgunClient {

    @PostMapping("sandbox242a0ce4ae1744f4b97fb068b5d64086.mailgun.org/messages")
    ResponseEntity<String> sendEmail(SendMailForm form);
}
