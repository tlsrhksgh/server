package com.example.server.member.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailComponent {
    private final JavaMailSender mailSender;

    @Async
    public void sendMail(String account, String mailContent) {
        log.info("EmailAsyncExecutor={}", Thread.currentThread().getName());
        SimpleMailMessage mailForm = new SimpleMailMessage();

        mailForm.setTo(account);
        mailForm.setFrom("admin@promise.com");
        mailForm.setSubject("약속 - 회원가입 인증번호 입니다.");
        mailForm.setText(mailContent);

        mailSender.send(mailForm);
    }
}
