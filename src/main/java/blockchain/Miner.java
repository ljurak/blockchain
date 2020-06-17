package blockchain;

public interface Miner {
    int getMinerId();
    int getCoins();
    void addCoins(int coins);
    void removeCoins(int coins);
    void mineNextBlock();
}
