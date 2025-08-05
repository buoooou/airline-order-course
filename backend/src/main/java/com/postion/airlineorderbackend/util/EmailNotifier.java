package com.postion.airlineorderbackend.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier {

    private final JavaMailSender mailSender;

    public EmailNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlert(String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("admin@example.com");
        mailMessage.setSubject("Flight Status Alert");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}