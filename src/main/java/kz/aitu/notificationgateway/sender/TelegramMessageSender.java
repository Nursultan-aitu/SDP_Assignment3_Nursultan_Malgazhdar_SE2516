package kz.aitu.notificationgateway.sender;

public final class TelegramMessageSender extends AbstractMessageSender {

    public TelegramMessageSender() {
        super();
    }

    public TelegramMessageSender(boolean available) {
        super(available);
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        String formattedBody = "*" + title + "*\n" + body;
        return deliver(recipient, title, formattedBody);
    }

    @Override
    public String getChannelName() {
        return "Telegram";
    }
}

