package com.github.demwafflez.networkutility;

import java.io.*;

public class ObjectUtility {
    public static byte[] writeData(Object object) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteOutput);

        outputStream.writeObject(object);

        return byteOutput.toByteArray();
    }
    public static Object readData(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
        ObjectInputStream inputStream = new ObjectInputStream(byteInput);

        return inputStream.readObject();
    }
}
