package kz.aitu.notificationgateway.notification;

import kz.aitu.notificationgateway.sender.MessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class EncryptedNotification extends Notification {

    public EncryptedNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        if (!sender.isAvailable()) {
            System.out.printf("Channel %s is unavailable.%n", sender.getChannelName());
            return;
        }

        String encodedMessage = Base64.getEncoder().encodeToString(
                message.getBytes(StandardCharsets.UTF_8)
        );
        if (!sender.sendMessage(recipient, "Encrypted Notification", encodedMessage)) {
            System.out.printf("Encrypted notification to %s was not delivered.%n", recipient);
        }
    }
}

