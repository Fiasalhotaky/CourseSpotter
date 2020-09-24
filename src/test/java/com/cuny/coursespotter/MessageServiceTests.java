package com.cuny.coursespotter;

import com.sendgrid.helpers.mail.objects.Content;
import com.twilio.Twilio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageServiceTests {

    public static final String TEST_SID = "AC125044e35898d5b0ee3c63b7c8588e14";
    public static final String TEST_TOKEN = "3f1a808a97c01a6fa52ddc5c2ccb6f62";
    public static final String SG_TEST_KEY = "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";

    MessageService ms = new MessageService(TEST_SID, TEST_TOKEN, SG_TEST_KEY);


    @Test
    public void sendEmailTestA() {
        int result = ms.sendEmail("admin@coursespotter", "CourseSpotter Notification", "test@example.com", new Content("text/plain", "Please disregard this message."));
        // 202	ACCEPTED  Your message is both valid, and queued to be delivered.
        assertEquals(202, result);
    }

    @Test
    public void sendEmailTestB() {
        int result = ms.sendEmail("admin@coursespotter", "CourseSpotter Notification", "Someone", new Content("text/plain", "Please disregard this message."));

        assertEquals(0, result);
    }

    @Test
    public void sendTextTestA() {
        Twilio.init(TEST_SID, TEST_TOKEN);

        String sid = ms.sendText("+16466939744", "+15005550006", "Hello");

        assertFalse(sid.isEmpty());
    }

    @Test
    public void sendTextTestB() {
        String sid = ms.sendText("+16466939744", "+16466939744", "Hello");
        assertTrue(sid.isEmpty());

    }
}
