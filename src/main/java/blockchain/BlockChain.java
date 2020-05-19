package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockChain implements Serializable {

    private List<Block> blocks = new ArrayList<>();

    private int difficulty;

    public BlockChain(int difficulty) {
        this.difficulty = difficulty;
    }

    public void addBlock() {
        long start = System.currentTimeMillis();

        Block block;
        if (blocks.size() == 0) {
            block = new Block(1, "0");
        } else {
            Block lastBlock = blocks.get(blocks.size() - 1);
            block = new Block(lastBlock.getId() + 1, lastBlock.getHash());
        }

        blocks.add(block);
        block.mineBlock(difficulty);

        System.out.println(block);
        System.out.println("Block was generating for " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
    }

    public boolean checkValidity() {
        if (blocks.size() == 0) {
            return true;
        }

        String prefix = "0".repeat(difficulty);

        if (blocks.size() == 1) {
            Block block = blocks.get(0);
            return block.getHash().equals(block.calculateHash()) &&
                    block.getHash().substring(0, difficulty).equals(prefix);
        }

        Block previousBlock;
        Block currentBlock;
        for (int i = 1; i < blocks.size(); i++) {
            previousBlock = blocks.get(i - 1);
            currentBlock = blocks.get(i);

            if (!(currentBlock.getHash().equals(currentBlock.calculateHash()) &&
                    previousBlock.getHash().equals(currentBlock.getPreviousHash()) &&
                    currentBlock.getHash().substring(0, difficulty).equals(prefix))) {
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
