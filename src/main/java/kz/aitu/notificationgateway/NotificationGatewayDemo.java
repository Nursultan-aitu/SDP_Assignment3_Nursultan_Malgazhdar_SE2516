package kz.aitu.notificationgateway;

import kz.aitu.notificationgateway.notification.EncryptedNotification;
import kz.aitu.notificationgateway.notification.Notification;
import kz.aitu.notificationgateway.notification.ScheduledBatchNotification;
import kz.aitu.notificationgateway.notification.UrgentNotification;
import kz.aitu.notificationgateway.sender.EmailMessageSender;
import kz.aitu.notificationgateway.sender.SmsMessageSender;
import kz.aitu.notificationgateway.sender.TelegramMessageSender;

import java.util.List;

public final class NotificationGatewayDemo {

    private NotificationGatewayDemo() {
    }

    public static void main(String[] args) {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        SmsMessageSender sms = new SmsMessageSender();

        printScenario("1. UrgentNotification + Telegram");
        Notification urgent = new UrgentNotification(telegram, sms);
        urgent.send("@nursultan", "The server needs attention.");

        printScenario("2. EncryptedNotification + Email");
        Notification encrypted = new EncryptedNotification(email);
        encrypted.send("student@example.com", "Confidential report");

        printScenario("3. ScheduledBatchNotification + SMS");
        ScheduledBatchNotification batch = new ScheduledBatchNotification(
                sms,
                List.of("+77010000001", "+77010000002", "+77010000003")
        );
        batch.sendBatch("The workshop starts at 10:00.");

        printScenario("4. Runtime switching: Telegram -> Email");
        Notification switchable = new UrgentNotification(telegram, sms);
        switchable.send("@nursultan", "First delivery uses Telegram.");
        switchable.setSender(email);
        switchable.send("student@example.com", "Second delivery uses Email.");

        printScenario("5. Telegram unavailable -> fallback to SMS");
        TelegramMessageSender unavailableTelegram = new TelegramMessageSender(false);
        Notification withFallback = new UrgentNotification(unavailableTelegram, sms);
        withFallback.send("+77010000004", "Use the backup channel.");
    }

    private static void printScenario(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}

