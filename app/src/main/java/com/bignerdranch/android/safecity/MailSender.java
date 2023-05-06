package com.bignerdranch.android.safecity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {
    public void sendMail(String term, String mailFrom, String mailTo, String password){
        Properties properties = new Properties();
        //Хост или IP-адрес почтового сервера
        properties.put("mail.smtp.host", "smtp.rambler.ru");
        //Требуется ли аутентификация для отправки сообщения
        properties.put("mail.smtp.auth", "true");
        //Порт для установки соединения
        properties.put("mail.smtp.socketFactory.port", "465");
        //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        //Создаем соединение для отправки почтового сообщения
        Session session = Session.getDefaultInstance(properties,
                //Аутентификатор - объект, который передает логин и пароль
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailFrom, password);
                    }
                });

       // session.setDebug(true);
        try {
            //Создаем новое почтовое сообщение
            MimeMessage messager = new MimeMessage(session);
            //От кого
            messager.setFrom(new InternetAddress(mailFrom));
            //Кому
            messager.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(mailTo));
            //Тема письма
            messager.setSubject("SafeCity вопросы и предложения");
            //Текст письма
            messager.setText(term);
            //Поехали!!!
            Transport.send(messager);
        }catch(MessagingException mex){
            mex.printStackTrace();
        }
    }
}
