package kz.aitu.notificationgateway;

import kz.aitu.notificationgateway.notification.BatchSummary;
import kz.aitu.notificationgateway.notification.EncryptedNotification;
import kz.aitu.notificationgateway.notification.MarketingNotification;
import kz.aitu.notificationgateway.notification.Notification;
import kz.aitu.notificationgateway.notification.ScheduledBatchNotification;
import kz.aitu.notificationgateway.notification.UrgentNotification;
import kz.aitu.notificationgateway.sender.AbstractMessageSender;
import kz.aitu.notificationgateway.sender.DeliveredMessage;
import kz.aitu.notificationgateway.sender.EmailMessageSender;
import kz.aitu.notificationgateway.sender.KafkaEventSender;
import kz.aitu.notificationgateway.sender.PushNotificationSender;
import kz.aitu.notificationgateway.sender.SmsMessageSender;
import kz.aitu.notificationgateway.sender.TelegramMessageSender;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationGatewayTest {

    @Test
    void telegramSendsFormattedMessage() {
        TelegramMessageSender telegram = new TelegramMessageSender();

        boolean sent = telegram.sendMessage("@student", "Status", "All systems ready");

        assertTrue(sent);
        DeliveredMessage delivered = telegram.getLastMessage().orElseThrow();
        assertEquals("Telegram", delivered.channel());
        assertTrue(delivered.body().contains("*Status*"));
    }

    @Test
    void emailKeepsTitleAndAddsSignature() {
        EmailMessageSender email = new EmailMessageSender();

        email.sendMessage("student@example.com", "Report", "Attached report");

        DeliveredMessage delivered = email.getLastMessage().orElseThrow();
        assertEquals("Report", delivered.title());
        assertTrue(delivered.body().contains("Regards,"));
    }

    @Test
    void smsAcceptsMessageAtLengthLimit() {
        SmsMessageSender sms = new SmsMessageSender();

        boolean sent = sms.sendMessage("+77010000001", "", "a".repeat(160));

        assertTrue(sent);
        assertEquals(1, sms.getSentMessages().size());
    }

    @Test
    void smsRejectsMessageOverLengthLimit() {
        SmsMessageSender sms = new SmsMessageSender();

        boolean sent = sms.sendMessage("+77010000001", "", "a".repeat(161));

        assertFalse(sent);
        assertTrue(sms.getSentMessages().isEmpty());
    }

    @Test
    void unavailableProviderDoesNotSend() {
        TelegramMessageSender telegram = new TelegramMessageSender(false);

        boolean sent = telegram.sendMessage("@student", "Test", "Message");

        assertFalse(sent);
        assertTrue(telegram.getSentMessages().isEmpty());
    }

    @Test
    void urgentNotificationUsesFallbackWhenPrimaryIsUnavailable() {
        TelegramMessageSender telegram = new TelegramMessageSender(false);
        SmsMessageSender sms = new SmsMessageSender();
        Notification urgent = new UrgentNotification(telegram, sms);

        urgent.send("+77010000002", "System alert");

        assertTrue(telegram.getSentMessages().isEmpty());
        assertEquals(1, sms.getSentMessages().size());
        assertTrue(sms.getLastMessage().orElseThrow().body().contains("[URGENT]"));
    }

    @Test
    void urgentNotificationUsesPrimaryWhenItIsAvailable() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        SmsMessageSender sms = new SmsMessageSender();
        Notification urgent = new UrgentNotification(telegram, sms);

        urgent.send("@student", "System alert");

        assertEquals(1, telegram.getSentMessages().size());
        assertTrue(sms.getSentMessages().isEmpty());
    }

    @Test
    void encryptedNotificationTransformsMessageWithBase64() {
        EmailMessageSender email = new EmailMessageSender();
        Notification encrypted = new EncryptedNotification(email);
        String originalMessage = "Private message";

        encrypted.send("student@example.com", originalMessage);

        String expected = Base64.getEncoder().encodeToString(
                originalMessage.getBytes(StandardCharsets.UTF_8)
        );
        assertTrue(email.getLastMessage().orElseThrow().body().startsWith(expected));
        assertFalse(email.getLastMessage().orElseThrow().body().contains(originalMessage));
    }

    @Test
    void batchNotificationReportsSuccessfulAndFailedDeliveries() {
        SelectiveSender sender = new SelectiveSender();
        ScheduledBatchNotification batch = new ScheduledBatchNotification(
                sender,
                List.of("user-1", "user-2", "user-3")
        );

        BatchSummary summary = batch.sendBatch("Batch message");

        assertEquals(3, summary.total());
        assertEquals(2, summary.successful());
        assertEquals(1, summary.failed());
    }

    @Test
    void sameNotificationCanSwitchSenderAtRuntime() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        Notification notification = new UrgentNotification(telegram);

        notification.send("@student", "First message");
        notification.setSender(email);
        notification.send("student@example.com", "Second message");

        assertEquals(1, telegram.getSentMessages().size());
        assertEquals(1, email.getSentMessages().size());
    }

    @Test
    void pushNotificationSenderDeliversMessage() {
        PushNotificationSender push = new PushNotificationSender();

        boolean sent = push.sendMessage("device-123", "Reminder", "Open the app");

        assertTrue(sent);
        assertEquals("Push Notification", push.getChannelName());
        assertEquals(1, push.getSentMessages().size());
    }

    @Test
    void marketingNotificationWorksWithExistingSender() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        Notification marketing = new MarketingNotification(telegram);

        marketing.send("@student", "Weekend discount");

        assertTrue(telegram.getLastMessage().orElseThrow().body().contains("[PROMO]"));
    }

    @Test
    void marketingNotificationWorksWithNewPushSender() {
        PushNotificationSender push = new PushNotificationSender();
        Notification marketing = new MarketingNotification(push);

        marketing.send("device-123", "Weekend discount");

        assertTrue(push.getLastMessage().orElseThrow().body().contains("[PROMO]"));
    }

    @Test
    void kafkaSenderPublishesSimulatedEvent() {
        KafkaEventSender kafka = new KafkaEventSender();

        boolean sent = kafka.sendMessage("user-1", "Account", "Account updated");

        assertTrue(sent);
        DeliveredMessage event = kafka.getLastMessage().orElseThrow();
        assertEquals("notification-events", event.recipient());
        assertTrue(event.body().startsWith("NotificationEvent{"));
    }

    private static final class SelectiveSender extends AbstractMessageSender {

        @Override
        public boolean sendMessage(String recipient, String title, String body) {
            if (recipient.endsWith("2")) {
                return false;
            }
            return deliver(recipient, title, body);
        }

        @Override
        public String getChannelName() {
            return "Selective Test Sender";
        }
    }
}

