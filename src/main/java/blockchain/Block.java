package blockchain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block implements Serializable {

    private long id;

    private int magic;

    private int minerId;

    private int difficulty;

    private long timestamp;

    private List<Transaction> transactions = new ArrayList<>();

    private String previousHash;

    private String hash;

    public Block(long id, int difficulty, String previousHash) {
        this.id = id;
        this.difficulty = difficulty;
        this.timestamp = Instant.now().toEpochMilli();
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public long getId() {
        return id;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getMinerId() {
        return minerId;
    }

    public void setMinerId(int minerId) {
        this.minerId = minerId;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public String calculateHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(magic).append(minerId).append(difficulty).append(timestamp).append(previousHash);
        for (Transaction transaction : transactions) {
            sb.append(transaction);
        }
        String inputData = sb.toString();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] inputBytes = inputData.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(inputBytes);

            StringBuilder hash = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    hash.append("0");
                }
                hash.append(hex);
            }
            return hash.toString();
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
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(id).append(System.lineSeparator());
        sb.append("Timestamp: ").append(timestamp).append(System.lineSeparator());
        sb.append("Magic number: ").append(magic).append(System.lineSeparator());
        sb.append("Hash of the previous block: ").append(previousHash).append(System.lineSeparator());
        sb.append("Hash of the block: ").append(hash).append(System.lineSeparator());
        sb.append("Block transactions:").append(System.lineSeparator());
        if (transactions.isEmpty()) {
            sb.append("no transactions").append(System.lineSeparator());
        } else {
            for (Transaction transaction : transactions) {
                sb.append(transaction).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
