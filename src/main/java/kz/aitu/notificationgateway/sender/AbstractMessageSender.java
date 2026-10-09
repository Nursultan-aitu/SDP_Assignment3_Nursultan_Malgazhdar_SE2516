package kz.aitu.notificationgateway.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractMessageSender implements MessageSender {

    private final List<DeliveredMessage> sentMessages = new ArrayList<>();
    private boolean available;

    protected AbstractMessageSender() {
        this(true);
    }

    protected AbstractMessageSender(boolean available) {
        this.available = available;
    }

    @Override
    public final boolean isAvailable() {
        return available;
    }

    public final void setAvailable(boolean available) {
        this.available = available;
    }

    public final List<DeliveredMessage> getSentMessages() {
        return List.copyOf(sentMessages);
    }

    public final Optional<DeliveredMessage> getLastMessage() {
        if (sentMessages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sentMessages.get(sentMessages.size() - 1));
    }

    protected final boolean deliver(String recipient, String title, String body) {
        if (!available) {
            return false;
        }

        DeliveredMessage message = new DeliveredMessage(
                getChannelName(), recipient, title, body
        );
        sentMessages.add(message);
        System.out.printf(
                "[%s] To: %s | Title: %s | Body: %s%n",
                message.channel(), message.recipient(), message.title(), message.body()
        );
        return true;
    }
}

