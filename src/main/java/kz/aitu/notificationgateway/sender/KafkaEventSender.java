package kz.aitu.notificationgateway.sender;

public final class KafkaEventSender extends AbstractMessageSender {

    public KafkaEventSender() {
        super();
    }

    public KafkaEventSender(boolean available) {
        super(available);
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        String event = "NotificationEvent{recipient='%s', title='%s', body='%s'}"
                .formatted(recipient, title, body);
        return deliver("notification-events", "Kafka event", event);
    }

    @Override
    public String getChannelName() {
        return "Kafka";
    }
}

