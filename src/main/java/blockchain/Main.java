package blockchain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Main {

    private static final int MINERS = 8;

    public static void main(String[] args) {
        /*Scanner scanner = new Scanner(System.in);
        String filename = args.length > 0 ? args[0] : null;

        BlockChain blockChain = loadBlockChain(filename);
        if (blockChain == null || !blockChain.checkValidity()) {
            System.out.print("Enter how many zeros the hash must start with: ");
            int difficulty = Integer.parseInt(scanner.nextLine());
            System.out.println();
            blockChain = new BlockChain(difficulty);
        }*/

        BlockChain blockChain = new BlockChain();

        MinerThread[] miners = new MinerThread[MINERS];

        for (int i = 0; i < MINERS; i++) {
            miners[i] = new MinerThread(blockChain, Integer.MIN_VALUE + i * (Integer.MAX_VALUE / 4), i + 1);
            blockChain.addMiner(miners[i]);
        }

        for (int i = 0; i < MINERS; i++) {
            miners[i].start();
        }

        /*saveBlocks(blockChain, filename);*/
    }

    private static void saveBlocks(BlockChain blockChain, String filename) {
        if (filename != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
                try {
                    out.writeObject(blockChain);
                } catch (IOException e) {
                    System.out.println("Error occurred: " + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Error occurred when trying to write file: " + filename);
            }
        }
    }

    private static BlockChain loadBlockChain(String filename) {
        if (filename != null) {
            File file = new File(filename);

            if (file.exists() && file.isFile()) {
                try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                    try {
                        return (BlockChain) in.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error occurred: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred when trying to read file: " + filename);
                }
            }
        }

        return null;
    }
}
