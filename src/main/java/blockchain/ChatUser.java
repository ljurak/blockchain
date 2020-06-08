package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class ChatUser extends Thread {

    private static final int BLOCK_LIMIT = 6;

    private final String name;

    private final BlockChain blockChain;

    public ChatUser(String name, BlockChain blockChain) {
        this.name = name;
        this.blockChain = blockChain;
    }

    @Override
    public void run() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.generateKeyPair();

            blockChain.setPublicKey(name, keyPair.getPublic());

            Signature signature = Signature.getInstance("SHA256withDSA");
            signature.initSign(keyPair.getPrivate());

            while (blockChain.size() < BLOCK_LIMIT) {
                String message = name + ": Test message";
                signature.update(message.getBytes(StandardCharsets.UTF_8));
                byte[] signatureBytes = signature.sign();

                if (blockChain.addMessage(new ChatMessage(message, name, signatureBytes))) {
                    System.out.println("Message accepted from: " + name);
                } else {
                    System.out.println("Message not accepted from: " + name);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (GeneralSecurityException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
}
