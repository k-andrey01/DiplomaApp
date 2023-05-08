package com.bignerdranch.android.safecity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

public class MailSender {
    public void sendMail(String term, String mailFrom, String mailTo, String password) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.rambler.ru");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailFrom, password);
                    }
                });
        try {
            MimeMessage messager = new MimeMessage(session);
            messager.setFrom(new InternetAddress(mailFrom));
            messager.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(mailTo));
            messager.setSubject("SafeCity вопросы и предложения");
            messager.setText(term);
            Transport.send(messager);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
