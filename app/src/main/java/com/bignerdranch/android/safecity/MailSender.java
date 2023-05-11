package com.bignerdranch.android.safecity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import android.os.AsyncTask;

public class MailSender {

    public void sendMail(String text, String mailFrom, String mailTo, String password, String theme) {
        new SendMailTask().execute(text, mailFrom, mailTo, password, theme);
    }

    private class SendMailTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String text = params[0];
            String mailFrom = params[1];
            String mailTo = params[2];
            String password = params[3];
            String theme = params[4];

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
                messager.setFrom(new InternetAddress(mailFrom, "SafeCity"));
                messager.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(mailTo));
                messager.setSubject(theme);
                messager.setText(text);
                Transport.send(messager);
            } catch (MessagingException | UnsupportedEncodingException mex) {
                mex.printStackTrace();
            }

            return null;
        }
    }
}

