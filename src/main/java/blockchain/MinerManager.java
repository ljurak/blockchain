package blockchain;

public interface MinerManager {
    void notifyMiners();
    void addMiner(Miner miner);
    void removeMiner(Miner miner);
}
