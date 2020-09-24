package com.cuny.coursespotter;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageService {

    private SendGrid sendGrid;

    public static final String accountSid = "ACcd3c0a4d1529ca0e1e7a3d656ba7a027";
    public static final String authToken = "13c7c05654175f148419c5841f9e26d6";
    public static final String sendGridKey = "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";

    public MessageService() {
        Twilio.init(accountSid, authToken);
        sendGrid = new SendGrid(sendGridKey);
    }

    public MessageService(String twilioSID, String twilioToken, String sendGridToken) {
        Twilio.init(twilioSID, twilioToken);
        sendGrid = new SendGrid(sendGridToken);
    }

    /**
     * Sends an email message to the recipient using the following subject and body.
     *
     * @param from    email address to send from
     * @param subject email subject line
     * @param to      email address to send to
     * @param content email body
     * @return
     */
    public int sendEmail(String from, String subject, String to, Content content) {
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            return response.getStatusCode();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Sends a text message to the recipient with the specified message.
     *
     * @param to   To number
     * @param from From number
     * @param body Message body
     * @return
     */
    public String sendText(String to, String from, String body) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    body
            ).create();

            return message.getSid();
        } catch (final ApiException e) {
            e.printStackTrace();
        }
        return "";
    }
}
