package kz.aitu.notificationgateway.notification;

import kz.aitu.notificationgateway.sender.MessageSender;

import java.util.Objects;

public abstract class Notification {

    protected MessageSender sender;

    protected Notification(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
    }

    public final void setSender(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
    }

    public abstract void send(String recipient, String message);
}

