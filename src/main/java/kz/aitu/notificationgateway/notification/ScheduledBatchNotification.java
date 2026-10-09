package kz.aitu.notificationgateway.notification;

import kz.aitu.notificationgateway.sender.MessageSender;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class ScheduledBatchNotification extends Notification {

    private final List<String> recipients;

    public ScheduledBatchNotification(
            MessageSender sender,
            Collection<String> recipients
    ) {
        super(sender);
        Objects.requireNonNull(recipients, "recipients must not be null");
        if (recipients.isEmpty()) {
            throw new IllegalArgumentException("recipients must not be empty");
        }
        this.recipients = List.copyOf(recipients);
    }

    public List<String> getRecipients() {
        return recipients;
    }

    @Override
    public void send(String recipient, String message) {
        sendBatch(message);
    }

    public BatchSummary sendBatch(String message) {
        int successful = 0;

        for (String recipient : recipients) {
            if (sender.isAvailable()
                    && sender.sendMessage(recipient, "Scheduled Batch", message)) {
                successful++;
            }
        }

        BatchSummary summary = new BatchSummary(
                recipients.size(), successful, recipients.size() - successful
        );
        System.out.println(summary);
        return summary;
    }
}

