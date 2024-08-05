package com.github.demwafflez.networkutility;

import java.net.InetAddress;

public class NetInfo {
    public final InetAddress address;
    public final int port;
    private final int hashCode;

    public NetInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        hashCode = toString().hashCode();
    }

    @Override
    public String toString() {
        return address.toString() + "  <:/>  " + port;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NetInfo && hashCode == obj.hashCode();
    }
}
