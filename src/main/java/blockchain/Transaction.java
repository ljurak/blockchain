package blockchain;

import java.util.Arrays;
import java.util.Objects;

public class Transaction {

    private final int senderId;

    private final int receiverId;

    private final int value;

    private final byte[] signature;

    public Transaction(int senderId, int receiverId, int value, byte[] signature) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.value = value;
        this.signature = signature;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public int getValue() {
        return value;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Transaction that = (Transaction) other;
        return senderId == that.senderId &&
                receiverId == that.receiverId &&
                value == that.value &&
                Arrays.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(senderId, receiverId, value);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return "miner" + senderId + " sent " + value + " VC to miner" + receiverId;
    }
}
