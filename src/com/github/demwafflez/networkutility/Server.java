package com.github.demwafflez.networkutility;

import java.io.IOException;

import static java.lang.Integer.*;

public class Server extends DatagramBase {
    public static void main(String[] args) throws IOException {
        new Server(args[0], parseInt(args[1]));
    }
    public Server(String address, int port) throws IOException {
        super(address, port);
    }
}
