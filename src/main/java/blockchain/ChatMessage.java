package blockchain;

public class ChatMessage {

    private final String message;

    private final String username;

    private final byte[] signature;

    public ChatMessage(String message, String username, byte[] signature) {
        this.message = message;
        this.username = username;
        this.signature = signature;
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
