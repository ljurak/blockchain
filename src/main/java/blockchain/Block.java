package blockchain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class Block implements Serializable {

    private long id;

    private long timestamp;

    private int magic;

    private String previousHash;

    private String hash;

    private int difficulty;

    public Block(long id, String previousHash, int difficulty) {
        this.id = id;
        this.difficulty = difficulty;
        this.timestamp = Instant.now().toEpochMilli();
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public String calculateHash() {
        String inputData = String.valueOf(id) + magic + difficulty + timestamp + previousHash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] inputBytes = inputData.getBytes(StandardCharsets.UTF_8);
            byte[] hash = digest.digest(inputBytes);

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void mineBlock() {
        magic++;
        hash = calculateHash();
    }

    @Override
    public String toString() {
        return "Id: " + id + System.lineSeparator() +
                "Timestamp: " + timestamp + System.lineSeparator() +
                "Magic number: " + magic + System.lineSeparator() +
                "Hash of the previous block: " + previousHash + System.lineSeparator() +
                "Hash of the block: " + hash;
    }
}
