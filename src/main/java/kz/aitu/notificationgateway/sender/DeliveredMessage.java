package kz.aitu.notificationgateway.sender;

public record DeliveredMessage(
        String channel,
        String recipient,
        String title,
        String body
) {
}

