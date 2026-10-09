package kz.aitu.notificationgateway.notification;

public record BatchSummary(int total, int successful, int failed) {

    @Override
    public String toString() {
        return "Total: %d%nSuccessful: %d%nFailed: %d"
                .formatted(total, successful, failed);
    }
}

