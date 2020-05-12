package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class BlockChain {

    private Block head;

    private int size = 0;

    public void addBlock() {
        if (head == null) {
            head = new Block(1, "0");
            size++;
            return;
        }

        Block current = head;
        while (current.next != null) {
            current = current.next;
        }

        current.next = new Block(current.id + 1, current.hash);
        size++;
    }

    public boolean validateBlockChain() {
        if (size == 0) {
            return true;
        }

        if (size == 1) {
            return head.hash.equals(head.calculateHash());
        }

        Block previous = head;
        Block current = previous.next;

        while (current != null) {
            if (!(current.hash.equals(current.calculateHash()) && previous.hash.equals(current.previousHash))) {
                return false;
            }

            previous = current;
            current = current.next;
        }

        return true;
    }

    private static class Block {

        private long id;

        private long timestamp;

        private String previousHash;

        private String hash;

        private Block next;

        private Block(long id, String previousHash) {
            this.id = id;
            this.timestamp = Instant.now().toEpochMilli();
            this.previousHash = previousHash;
            this.hash = calculateHash();
            System.out.println(toString());
        }

        private String calculateHash() {
            String inputData = String.valueOf(id) + timestamp + previousHash;
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

        @Override
        public String toString() {
            return "Block:" + System.lineSeparator() +
                    "Id: " + id + System.lineSeparator() +
                    "Timestamp: " + timestamp + System.lineSeparator() +
                    "Previous hash: " + previousHash + System.lineSeparator() +
                    "Hash: " + hash + System.lineSeparator();
        }
    }
}
