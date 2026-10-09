package kz.aitu.notificationgateway.notification;

import kz.aitu.notificationgateway.sender.MessageSender;

import java.util.Objects;

public final class UrgentNotification extends Notification {

    private MessageSender fallbackSender;

    public UrgentNotification(MessageSender sender) {
        super(sender);
    }

    public UrgentNotification(MessageSender sender, MessageSender fallbackSender) {
        super(sender);
        this.fallbackSender = Objects.requireNonNull(
                fallbackSender, "fallbackSender must not be null"
        );
    }

    public void setFallbackSender(MessageSender fallbackSender) {
        this.fallbackSender = Objects.requireNonNull(
                fallbackSender, "fallbackSender must not be null"
        );
    }

    @Override
    public void send(String recipient, String message) {
        String urgentMessage = "[URGENT] " + message;

        if (trySend(sender, recipient, urgentMessage)) {
            return;
        }

        if (fallbackSender != null && fallbackSender != sender) {
            System.out.printf(
                    "Primary channel %s failed. Trying fallback channel %s.%n",
                    sender.getChannelName(), fallbackSender.getChannelName()
            );
            if (trySend(fallbackSender, recipient, urgentMessage)) {
                return;
            }
        }

        System.out.printf("Urgent notification to %s was not delivered.%n", recipient);
    }

    private boolean trySend(MessageSender selectedSender, String recipient, String message) {
        return selectedSender.isAvailable()
                && selectedSender.sendMessage(recipient, "Urgent Notification", message);
    }
}

