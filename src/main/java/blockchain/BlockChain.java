package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockChain implements Serializable, MinerManager {

    private List<Block> blocks = new ArrayList<>();

    private List<Miner> miners = new ArrayList<>();

    private List<String> messages = new ArrayList<>();

    private Block nextBlock;

    private volatile int size;

    private int difficulty;

    public BlockChain() {
        this(0);
    }

    public BlockChain(int difficulty) {
        this.difficulty = difficulty;
        this.nextBlock = new Block(1, buildMessageString(), "0", difficulty);
    }

    public synchronized Block getNextBlock() {
        return new Block(nextBlock.getId(), nextBlock.getData(), nextBlock.getPreviousHash(), nextBlock.getDifficulty());
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

    public synchronized void addMessage(String message) {
        messages.add(message);
    }

    public boolean acceptBlock(Block block, long generationTime) {
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
        }

        if (isBlockValid) {
            blocks.add(block);
            size++;

            if (generationTime < 10) {
                difficulty++;
            } else if (generationTime > 60) {
                difficulty--;
            }

            nextBlock = new Block(block.getId() + 1, buildMessageString(), block.getHash(), difficulty);
            messages.clear();

            notifyMiners();
            return true;
        }

        return false;
    }

    private String buildMessageString() {
        if (!messages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String message : messages) {
                sb.append(message).append(System.lineSeparator());
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }

        return "";
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
