package blockchain;

public class ChatMessage {

    private final long id;

    private final String message;

    private final String username;

    private final byte[] signature;

    public ChatMessage(long id, String message, String username, byte[] signature) {
        this.id = id;
        this.message = message;
        this.username = username;
        this.signature = signature;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getSignature() {
        return signature;
    }
}
