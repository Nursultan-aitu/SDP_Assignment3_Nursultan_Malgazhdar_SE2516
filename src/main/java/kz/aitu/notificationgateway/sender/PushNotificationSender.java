package kz.aitu.notificationgateway.sender;

public final class PushNotificationSender extends AbstractMessageSender {

    public PushNotificationSender() {
        super();
    }

    public PushNotificationSender(boolean available) {
        super(available);
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        String compactBody = title + " | " + body;
        return deliver(recipient, "Mobile push", compactBody);
    }

    @Override
    public String getChannelName() {
        return "Push Notification";
    }
}

