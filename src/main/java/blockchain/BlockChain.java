package blockchain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChain implements Serializable, MinerManager {

    private static final int AWARD = 100;

    private List<Block> blocks = new ArrayList<>();

    private List<Miner> miners = new ArrayList<>();

    private List<Transaction> previousTransactions = new ArrayList<>();

    private List<Transaction> currentTransactions = new ArrayList<>();

    private Map<Integer, PublicKey> keys = new HashMap<>();

    private Signature signature = Signature.getInstance("SHA256withDSA");

    private Block nextBlock;

    private volatile int size;

    private int difficulty;

    public BlockChain() throws NoSuchAlgorithmException {
        this(0);
    }

    public BlockChain(int difficulty) throws NoSuchAlgorithmException {
        this.difficulty = difficulty;
        this.nextBlock = new Block(1, difficulty, "0");
    }

    public synchronized Block getNextBlock() {
        Block block = new Block(
                nextBlock.getId(),
                nextBlock.getDifficulty(),
                nextBlock.getPreviousHash());
        block.setTransactions(nextBlock.getTransactions());
        return block;
    }

    public int size() {
        return size;
    }

    @Override
    public void notifyMiners() {
        for (Miner miner : miners) {
            miner.mineNextBlock();
        }
    }

    @Override
    public void addMiner(Miner miner) {
        miners.add(miner);
    }

    @Override
    public void removeMiner(Miner miner) {
        for (int i = 0; i < miners.size(); i++) {
            if (miners.get(i).equals(miner)) {
                miners.remove(i);
                return;
            }
        }
    }

    private Miner getMiner(int minerId) {
        for (Miner miner : miners) {
            if (miner.getMinerId() == minerId) {
                return miner;
            }
        }
        return null;
    }

    public synchronized void setPublicKey(int minerId, PublicKey key) {
        keys.put(minerId, key);
    }

    public synchronized void addTransaction(Transaction transaction) {
        PublicKey key = keys.get(transaction.getSenderId());

        if (key != null) {
            try {
                String transactionData = String.valueOf(transaction.getSenderId()) +
                        transaction.getReceiverId() +
                        transaction.getValue();
                signature.initVerify(key);
                signature.update(transactionData.getBytes(StandardCharsets.UTF_8));

                if (signature.verify(transaction.getSignature())) {
                    Miner sender = getMiner(transaction.getSenderId());
                    Miner receiver = getMiner(transaction.getReceiverId());

                    if (sender == null || receiver == null) {
                        return;
                    }

                    if (transaction.getValue() <= sender.getCoins()) {
                        sender.removeCoins(transaction.getValue());
                        receiver.addCoins(transaction.getValue());
                        currentTransactions.add(transaction);
                    }
                }
            } catch (InvalidKeyException | SignatureException e) {
                System.out.println("Error occurred: " + e.getMessage());
            }
        }
    }

    public synchronized void acceptBlock(Block block, long generationTime) {
        String prefix = "0".repeat(difficulty);

        boolean isBlockValid = false;
        if (blocks.size() == 0 &&
                block.getHash().equals(block.calculateHash()) &&
                block.getHash().substring(0, difficulty).equals(prefix)) {
            isBlockValid = true;
        } else {
            Block lastBlock = blocks.get(blocks.size() - 1);

            if (block.getHash().equals(block.calculateHash()) &&
                    lastBlock.getHash().equals(block.getPreviousHash()) &&
                    block.getHash().substring(0, difficulty).equals(prefix)) {
                isBlockValid = true;
            }

            if (block.getTransactions().size() != previousTransactions.size()) {
                isBlockValid = false;
            } else {
                for (int i = 0; i < previousTransactions.size(); i++) {
                    if (!block.getTransactions().get(i).equals(previousTransactions.get(i))) {
                        isBlockValid = false;
                        break;
                    }
                }
            }
        }

        if (isBlockValid) {
            blocks.add(block);
            size++;

            /*if (generationTime < 10) {
                difficulty++;
            } else if (generationTime > 60) {
                difficulty--;
            }*/

            nextBlock = new Block(block.getId() + 1, difficulty, block.getHash());
            nextBlock.setTransactions(currentTransactions);

            previousTransactions = currentTransactions;
            currentTransactions = new ArrayList<>();

            Miner miner = getMiner(block.getMinerId());
            if (miner != null) {
                miner.addCoins(AWARD);
            }
            notifyMiners();

            printInfo(block, generationTime);
        }
    }

    private void printInfo(Block block, long generationTime) {
        System.out.println("Block:");
        System.out.println("Created by miner" + block.getMinerId());
        System.out.println("miner" + block.getMinerId() + " gets " + AWARD + " VC");
        System.out.print(block);
        System.out.println("Block was generating for " + generationTime + " seconds");
        System.out.println("N stays the same");
        /*System.out.println(generationTime < 10
                ? "N was increased to " + difficulty : generationTime > 60
                ? "N was decreased to " + difficulty : "N stays the same");*/
        System.out.println();
    }

    public boolean checkValidity() {
        if (blocks.size() == 0) {
            return true;
        }

        if (blocks.size() == 1) {
            Block block = blocks.get(0);
            String prefix = "0".repeat(block.getDifficulty());
            return block.getHash().equals(block.calculateHash()) &&
                    block.getHash().substring(0, block.getDifficulty()).equals(prefix);
        }

        Block previousBlock;
        Block currentBlock;
        for (int i = 1; i < blocks.size(); i++) {
            previousBlock = blocks.get(i - 1);
            currentBlock = blocks.get(i);

            String prefix = "0".repeat(currentBlock.getDifficulty());
            if (!(currentBlock.getHash().equals(currentBlock.calculateHash()) &&
                    previousBlock.getHash().equals(currentBlock.getPreviousHash()) &&
                    currentBlock.getHash().substring(0, currentBlock.getDifficulty()).equals(prefix))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (blocks.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Block block : blocks) {
            sb.append(block).append("\n\n");
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
