package kz.aitu.notificationgateway.sender;

public final class SmsMessageSender extends AbstractMessageSender {

    public static final int MAX_LENGTH = 160;

    public SmsMessageSender() {
        super();
    }

    public SmsMessageSender(boolean available) {
        super(available);
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        String message = title == null || title.isBlank()
                ? body
                : title + ": " + body;

        if (message.length() > MAX_LENGTH) {
            System.out.printf("[SMS] Message to %s rejected: more than %d characters.%n",
                    recipient, MAX_LENGTH);
            return false;
        }
        return deliver(recipient, "SMS", message);
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}

