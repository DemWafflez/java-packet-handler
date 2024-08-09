package example;

import com.github.demwafflez.networkutility.Client;
import com.github.demwafflez.networkutility.Packet;

import java.io.IOException;

public class Main {
    private static final String[] words = {"hello", "man", "good", "I", "I'm", "dog", "cat", "wolf", "elephant", "crazy", "you", "you're", "a", "cool"};
    public static void main(String[] args) throws IOException {
        ServerMessageReceiver receiver = new ServerMessageReceiver("localhost", 54321);

        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException _) {}

        Client messager = new Client("localhost", 54321);

        // test send massive packet
        messager.sendPacket(new Packet(generateRandomMessage(100000)));

        /*
            Server receives message and prints this
            /127.0.0.1  <:/>  52796 Size: 477880 : a wolf cat wolf man I'm hello I'm wolf good........
         */
    }
    public static String generateRandomMessage(int length) {
        StringBuilder message = new StringBuilder();

        for(int i=0;i<length;i++) {
            int index = (int) (Math.random() * words.length);
            message.append(words[index]).append(" ");
        }
        return message.toString() + "\r";
    }
}
