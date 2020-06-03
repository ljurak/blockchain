package blockchain;

public class MinerThread extends Thread implements Miner {

    private static final int BLOCK_LIMIT = 6;

    private final BlockChain blockChain;

    private final int magic;

    private final int minerId;

    private volatile boolean isMined = false;

    public MinerThread(BlockChain blockChain, int magic, int minerId) {
        this.blockChain = blockChain;
        this.magic = magic;
        this.minerId = minerId;
    }

    @Override
    public void mineNextBlock() {
        isMined = true;
    }

    @Override
    public void run() {
        while (blockChain.size() < BLOCK_LIMIT) {
            isMined = false;
            Block block = blockChain.getNextBlock();
            block.setMagic(magic);

            String prefix = "0".repeat(block.getDifficulty());
            while (!isMined) {
                block.mineBlock();

                if (block.getHash().substring(0, block.getDifficulty()).equals(prefix)) {
                    long generationTime = (System.currentTimeMillis() - block.getTimestamp()) / 1000;

                    synchronized (blockChain) {
                        boolean accepted = blockChain.acceptBlock(block, generationTime);

                        if (accepted) {
                            System.out.println("Block:");
                            System.out.println("Created by miner # " + minerId);
                            System.out.println(block);
                            System.out.println("Block was generating for " + generationTime + " seconds");
                            System.out.println(generationTime < 10
                                    ? "N was increased to " + (block.getDifficulty() + 1) : generationTime > 60
                                    ? "N was decreased to " + (block.getDifficulty() - 1) : "N stays the same");
                            System.out.println();
                        }
                    }
                }
            }
        }
    }
}
