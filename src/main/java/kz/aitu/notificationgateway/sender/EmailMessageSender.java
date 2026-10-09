package kz.aitu.notificationgateway.sender;

public final class EmailMessageSender extends AbstractMessageSender {

    private static final String SIGNATURE = "\n\nRegards,\nNotification Gateway";

    public EmailMessageSender() {
        super();
    }

    public EmailMessageSender(boolean available) {
        super(available);
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        return deliver(recipient, title, body + SIGNATURE);
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}

