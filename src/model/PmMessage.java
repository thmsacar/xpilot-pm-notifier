package model;

/**
 * Data model representing a single Private Message (PM) extracted from network logs.
 */
public class PmMessage {
    private String timestamp;
    private String sender;
    private String receiver;
    private String content;

    /**
     * Constructs a new PmMessage.
     * @param timestamp Time of the message in HH:mm:ss format.
     * @param sender Callsign of the sender.
     * @param receiver Callsign of the recipient.
     * @param content The actual message text.
     */
    public PmMessage(String timestamp, String sender, String receiver, String content) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    /**
     * Formats the message for display in the log area.
     * @return Formatted string representation of the message.
     */
    @Override
    public String toString() {
        // Formats output like: [14:30:00] IST_W_APP: Please contact me!
        return String.format("[%s] %s: %s", timestamp, sender, content);
    }
}