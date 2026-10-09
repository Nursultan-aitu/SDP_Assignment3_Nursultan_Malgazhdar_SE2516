package kz.aitu.notificationgateway.notification;

import kz.aitu.notificationgateway.sender.MessageSender;

public final class MarketingNotification extends Notification {

    public MarketingNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        if (!sender.isAvailable()) {
            System.out.printf("Channel %s is unavailable.%n", sender.getChannelName());
            return;
        }

        String marketingMessage = "[PROMO] " + message;
        if (!sender.sendMessage(recipient, "Special Offer", marketingMessage)) {
            System.out.printf("Marketing notification to %s was not delivered.%n", recipient);
        }
    }
}

