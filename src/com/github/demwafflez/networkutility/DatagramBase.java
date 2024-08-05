package com.github.demwafflez.networkutility;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatagramBase {
    private final DatagramSocket socket;
    private final Map<Long, Message> messages = new ConcurrentHashMap<>();
    private final DatagramPacket lastPacket = new DatagramPacket(new byte[Message.MAX_PACKET_SIZE], Message.MAX_PACKET_SIZE);
    private final ByteBuffer lastPacketBuffer = ByteBuffer.wrap(lastPacket.getData());

    public DatagramBase() throws IOException {
        socket = new DatagramSocket();
        setupThreads();
        createShutdownHook();
    }
    public DatagramBase(String address, int port) throws IOException {
        InetAddress add = InetAddress.getByName(address);
        socket = new DatagramSocket(port, add);

        setupThreads();
        createShutdownHook();
    }
    private void sendPacket(InetAddress address, int port, byte[] data) throws IOException {
        int size = Message.MAX_ARRAY_SIZE;
        int length = Message.getNumPackets(data);

        Message message = new Message(length);

        for(int i=0;i<length;i++) {
            int start = i * size;
            int end = (i == length - 1) ? data.length : (i + 1) * size;

            byte[] arr = new byte[end - start];
            System.arraycopy(data, start, arr, 0, arr.length);

            message.addChunk(arr);
        }
        message.sendChunks(address, port, socket);
    }
    private void sendPacket(NetInfo info, byte[] data) throws IOException {
        sendPacket(info.address, info.port, data);
    }
    public void sendPacket(NetInfo info, Object object) throws IOException {
        sendPacket(info, ObjectUtility.writeData(object));
    }
    public void receivePacket() throws IOException {
        socket.setSoTimeout(2500);
        socket.receive(lastPacket);

        long id = lastPacketBuffer.getLong();
        int messageLength = lastPacketBuffer.getInt();
        int index = lastPacketBuffer.getInt();

        byte[] array = new byte[lastPacket.getLength()];
        System.arraycopy(lastPacketBuffer.array(), 0, array, 0, array.length);

        Message message = messages.get(id);

        if(message == null) {
            message = new Message(id, messageLength);
            message.setSenderInfo(new NetInfo(lastPacket.getAddress(), lastPacket.getPort()));
            messages.put(id, message);
        }
        message.putChunkSerialized(array, index);
        lastPacketBuffer.rewind();
    }
    private Message getNextMessage() {
        for(Message message : messages.values()) {
            if(message.ready()) {
                messages.remove(message.id);
                return message;
            }
        }
        return null;
    }
    public void setupThreads() {
        createPacketListenerThread().start();
        createPacketHandlerThread().start();
    }
    private Thread createPacketHandlerThread() {
        Thread thread = new Thread(() -> {
            while(!isClosed()) {
                Message message = getNextMessage();
                if(message == null) continue;

                try {
                    handlePacket(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setName("PacketHandler");
        return thread;
    }
    protected void handlePacket(Message message) throws IOException {}
    private Thread createPacketListenerThread()  {
        Thread thread = new Thread(() -> {
            while(!isClosed()) {
                try {
                    receivePacket();
                }
                catch(SocketTimeoutException | SocketException ex) {
                    continue;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setName("PacketListener");

        return thread;
    }
    private void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
            System.out.println("Successfully closed socket");
        }));
    }
    public boolean isClosed() {
        return socket.isClosed();
    }
    public void close() {
        if(isClosed()) return;
        socket.close();
    }
}
