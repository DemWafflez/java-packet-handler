package example;

import com.github.demwafflez.networkutility.Message;
import com.github.demwafflez.networkutility.Packet;
import com.github.demwafflez.networkutility.Server;

import java.io.IOException;

public class ServerMessageReceiver extends Server {
    public ServerMessageReceiver(String address, int port) throws IOException {
        super(address, port);
    }

    @Override
    protected void handlePacket(Message message) {
        super.handlePacket(message);
        try {
            Packet packet = (Packet) message.getDataObject();
            System.out.println(
                    message.getSenderInfo() +
                    " Size: " + message.getTotalSize() +
                    " : " +
                    packet.next()
            );
        }
        catch(ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
