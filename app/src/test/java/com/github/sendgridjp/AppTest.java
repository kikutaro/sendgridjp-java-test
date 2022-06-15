package com.github.sendgridjp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void メール送信テスト() throws IOException
    {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        System.out.println(apiKey);
        List<Email> tos = Arrays.asList(dotenv.get("TOS").split((","))).stream().map(to -> new Email(to)).collect(Collectors.toList());
        Email from = new Email(dotenv.get("FROM"), "送信者");

        String subject = "[sendgrid-java-example] フクロウのお名前はfullnameさん";
        String plainTextContent = "familyname さんは何をしていますか？\r\n 彼はplaceにいます。";
        String htmlContent = "<strong> familyname さんは何をしていますか？</strong><br />彼はplaceにいます。";
        Mail msg = new Mail();
        msg.setSubject(subject);
        msg.setFrom(from);
        msg.addContent(new Content("text/plain", plainTextContent));
        msg.addContent(new Content("text/html", htmlContent));
        msg.addCategory("category1");
        msg.addHeader("X-Sent-Using", "SendGrid-API");
        
        Attachments attachment = new Attachments();
        attachment.setContent(Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("src/test/resources/gif.gif"))));
        attachment.setFilename("owl.gif");
        attachment.setType("image/gif");
        attachment.setDisposition("attachment");
        msg.addAttachments(attachment);

        Personalization p1 = new Personalization();
        p1.addTo(tos.get(0));
        p1.addSubstitution("fullname", "田中 太郎");
        p1.addSubstitution("familyname", "田中");
        p1.addSubstitution("place", "中野");
        msg.addPersonalization(p1);

        Personalization p2 = new Personalization();
        p2.addTo(tos.get(1));
        p2.addSubstitution("fullname", "佐藤 次郎");
        p2.addSubstitution("familyname", "佐藤");
        p2.addSubstitution("place", "目黒");
        msg.addPersonalization(p2);

        Personalization p3 = new Personalization();
        p3.addTo(tos.get(2));
        p3.addSubstitution("fullname", "鈴木 三郎");
        p3.addSubstitution("familyname", "鈴木");
        p3.addSubstitution("place", "中野");
        msg.addPersonalization(p3);

        SendGrid sg = new SendGrid(apiKey);
        Request req = new Request();
        req.setMethod(Method.POST);
        req.setEndpoint("mail/send");
        req.setBody(msg.build());
        Response response = sg.api(req);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());



        assertTrue(response.getStatusCode() == 200);
    }
}
