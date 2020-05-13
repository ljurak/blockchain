package blockchain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlockChain implements Serializable {

    private List<Block> blocks = new ArrayList<>();

    private int difficulty;

    public BlockChain(int difficulty) {
        this.difficulty = difficulty;
    }

    public void addBlock() {
        if (blocks.size() == 0) {
            blocks.add(new Block(1, "0", difficulty));
            return;
        }

        Block lastBlock = blocks.get(blocks.size() - 1);
        blocks.add(new Block(lastBlock.id + 1, lastBlock.hash, difficulty));
    }

    public boolean checkValidity() {
        if (blocks.size() == 0) {
            return true;
        }

        String prefix = "0".repeat(difficulty);

        if (blocks.size() == 1) {
            Block block = blocks.get(0);
            return block.hash.equals(block.calculateHash()) && block.hash.substring(0, difficulty).equals(prefix);
        }

        Block previousBlock;
        Block currentBlock;
        for (int i = 1; i < blocks.size(); i++) {
            previousBlock = blocks.get(i - 1);
            currentBlock = blocks.get(i);

            if (!(currentBlock.hash.equals(currentBlock.calculateHash()) &&
                    previousBlock.hash.equals(currentBlock.previousHash) &&
                    currentBlock.hash.substring(0, difficulty).equals(prefix))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block block : blocks) {
            sb.append(block).append("\n\n");
        }
        return sb.toString();
    }

    private static class Block implements Serializable {

        private long id;

        private long timestamp;

        private int magic;

        private String previousHash;

        private String hash;

        private Block(long id, String previousHash, int difficulty) {
            long start = System.currentTimeMillis();
            this.id = id;
            this.timestamp = Instant.now().toEpochMilli();
            this.previousHash = previousHash;
            this.hash = mineBlock(difficulty);
            System.out.println(toString());
            System.out.println("Block was generating for " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
        }

        private String calculateHash() {
            String inputData = String.valueOf(id) + magic + timestamp + previousHash;
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

        private String mineBlock(int difficulty) {
            String prefix = "0".repeat(difficulty);
            String hashValue = calculateHash();
            while (!hashValue.substring(0, difficulty).equals(prefix)) {
                magic++;
                hashValue = calculateHash();
            }
            return hashValue;
        }

        @Override
        public String toString() {
            return "Block:" + System.lineSeparator() +
                    "Id: " + id + System.lineSeparator() +
                    "Timestamp: " + timestamp + System.lineSeparator() +
                    "Magic number: " + magic + System.lineSeparator() +
                    "Hash of the previous block: " + previousHash + System.lineSeparator() +
                    "Hash of the block: " + hash;
        }
    }
}
