package com.github.demwafflez.networkutility;

import java.io.IOException;
import java.net.InetAddress;

import static java.lang.Integer.*;

public class Client extends DatagramBase {
    public static void main(String[] args) throws IOException {
        new Client(args[0], parseInt(args[1]));
    }

    protected NetInfo serverInfo;
    public Client(String serverAddress, int serverPort) throws IOException {
        super();
        serverInfo = new NetInfo(InetAddress.getByName(serverAddress), serverPort);
    }
    public void sendPacket(Object object) throws IOException {
        sendPacket(serverInfo, object);
    }
}
