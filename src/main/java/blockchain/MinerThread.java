package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public class MinerThread extends Thread implements Miner {

    private static final int BLOCK_LIMIT = 15;

    private final BlockChain blockChain;

    private final int magic;

    private final int minerId;

    private volatile boolean isMined = false;

    private volatile int coins = 100;

    public MinerThread(BlockChain blockChain, int magic, int minerId) {
        this.blockChain = blockChain;
        this.magic = magic;
        this.minerId = minerId;
    }

    @Override
    public int getMinerId() {
        return minerId;
    }

    public int getCoins() {
        return coins;
    }

    @Override
    public synchronized void addCoins(int coins) {
        this.coins += coins;
    }

    @Override
    public synchronized void removeCoins(int coins) {
        this.coins -= coins;
    }

    @Override
    public void mineNextBlock() {
        isMined = true;
    }

    @Override
    public void run() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            SecureRandom random = new SecureRandom();
            keyGen.initialize(1024, random);

            KeyPair keyPair = keyGen.generateKeyPair();
            blockChain.setPublicKey(minerId, keyPair.getPublic());

            Signature signature = Signature.getInstance("SHA256withDSA");
            signature.initSign(keyPair.getPrivate());

            while (blockChain.size() < BLOCK_LIMIT) {
                if (coins > 0) {
                    int receiverId = random.nextInt(4) + 1;
                    int amount = random.nextInt(100) + 1;

                    if (receiverId != minerId) {
                        String transactionData = String.valueOf(minerId) + receiverId + amount;
                        signature.update(transactionData.getBytes(StandardCharsets.UTF_8));
                        byte[] signatureBytes = signature.sign();
                        blockChain.addTransaction(new Transaction(minerId, receiverId, amount, signatureBytes));
                    }
                }

                isMined = false;
                Block block = blockChain.getNextBlock();
                block.setMagic(magic);
                block.setMinerId(minerId);

                String prefix = "0".repeat(block.getDifficulty());
                while (!isMined) {
                    block.mineBlock();

                    if (block.getHash().substring(0, block.getDifficulty()).equals(prefix)) {
                        long generationTime = (System.currentTimeMillis() - block.getTimestamp()) / 1000;
                        blockChain.acceptBlock(block, generationTime);
                    }
                }
            }
        } catch (GeneralSecurityException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
}
