package com.github.demwafflez.networkutility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public class Message  {
    public static final int PACKET_METADATA_SIZE = 16;
    public static final int MAX_PACKET_SIZE = 4096;
    public static final int MAX_ARRAY_SIZE = MAX_PACKET_SIZE - PACKET_METADATA_SIZE;

    public final long id;

    private final ConcurrentHashMap<Integer, byte[]> chunks;
    private final int messageLength;
    private int index = 0;
    private NetInfo senderInfo;

    public Message(long id, int messageLength) {
        this.id = id;
        this.messageLength = messageLength;
        chunks = new ConcurrentHashMap<>();
    }
    public Message(int messageLength) {
        this.id = generateId();
        this.messageLength = messageLength;
        chunks = new ConcurrentHashMap<>();
    }
    public static int getNumPackets(byte[] array) {
        return (int) Math.ceil((float) array.length / MAX_ARRAY_SIZE);
    }
    private long generateId() {
        return (long) (Math.random() * Math.pow(10, 12));
    }
    public void addChunk(byte[] data) {
        if(index >= messageLength || data.length > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException("ERROR ADDING CHUNK");
        }
        ByteBuffer buffer = ByteBuffer.allocate(data.length + PACKET_METADATA_SIZE);
        buffer.putLong(id);
        buffer.putInt(messageLength);
        buffer.putInt(index);
        buffer.put(data);

        putChunkSerialized(buffer.array(), index);
        index++;
    }
    public void putChunkSerialized(byte[] data, int index) {
        chunks.put(index, data);
    }
    public void sendChunks(InetAddress address, int port, DatagramSocket socket) throws IOException {
        if(!ready()) return;

        for(int i=0;i<messageLength;i++) {
            byte[] data = chunks.get(i);

            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        }
    }
    public byte[] getRawData() {
        byte[] array = new byte[getTotalSize()];
        int index = 0;

        for(int i=0;i<messageLength;i++) {
            byte[] byteArray = chunks.get(i);

            int readLength = byteArray.length - PACKET_METADATA_SIZE;
            System.arraycopy(byteArray, PACKET_METADATA_SIZE, array, index, readLength);
            index += readLength;
        }
        return array;
    }
    public Object getDataObject() throws IOException, ClassNotFoundException {
        return ObjectUtility.readData(getRawData());
    }
    public boolean ready() {
        return chunks.size() == messageLength;
    }
    public int getTotalSize() {
        int total = 0;

        for(int i=0;i<messageLength;i++) {
            byte[] byteArray = chunks.get(i);

            total += byteArray.length - PACKET_METADATA_SIZE;
        }
        return total;
    }

    public NetInfo getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(NetInfo senderInfo) {
        this.senderInfo = senderInfo;
    }

    @Override
    public String toString() {
        return id + " " + messageLength + " " + index;
    }
}
