import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class TestMailSend {
    public static void main(String[] args) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.yandex.ru");
        mailSender.setPort(465);
        mailSender.setUsername("stadivan@yandex.ru");
        mailSender.setPassword("bwzbxsspzcrqawnx");

        Properties props = mailSender.getJavaMailProperties();
        props.setProperty("mail.transport.protocol", "smtps");
        props.setProperty("mail.debug", "true");

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("stadivan@yandex.ru");
        mailMessage.setTo("dateli6645@miqlab.com");
        mailMessage.setSubject("Activate");
        mailMessage.setText("Hello, world!");

        mailSender.send(mailMessage);
    }
}
