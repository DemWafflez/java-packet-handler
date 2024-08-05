package com.github.demwafflez.networkutility;

import java.io.Serializable;

public class Packet implements Serializable {
    private final Object[] values;
    private transient int index;

    public Packet(Object... values) {
        this.values = values;
    }
    public Object next() {
        if(index >= values.length) return null;

        Object obj = values[index];
        index++;

        return obj;
    }
    public void reset() {
        index = 0;
    }
    public Object get(int index) {
        return values[index];
    }
}
