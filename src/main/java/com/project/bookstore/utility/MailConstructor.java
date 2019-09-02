package com.project.bookstore.utility;

import com.project.bookstore.domain.Order;
import com.project.bookstore.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Component
public class MailConstructor {

    @Value("${spring.mail.username}")
    private String supportEmail;

    @Autowired
    private TemplateEngine templateEngine;


    public MimeMessagePreparator constructPasswordResetEmail(String contextPath, String token, User user) {


        String url = contextPath + "/resetPassword?token=" + token;

        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("user", user);

        String text = templateEngine.process("passwordEmailMessage", context);

        MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
                email.setTo(user.getEmail());
                email.setSubject("Bookstore Password Reset");
                email.setText(text, true);
                email.setFrom(supportEmail);
            }
        };
        return messagePreparator;
    }

    public MimeMessagePreparator constructOrderConfirmationEmail(Order order, User user) {

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", user);

        String text = templateEngine.process("orderConfirmEmailMessage", context);

        MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
                email.setTo(user.getEmail());
                email.setSubject("Your Bookstore order");
                email.setText(text, true);
                email.setFrom(supportEmail);
            }
        };
        return messagePreparator;
    }
}
