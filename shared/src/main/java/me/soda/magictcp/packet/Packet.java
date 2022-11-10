package me.soda.magictcp.packet;

import java.io.Serializable;

public class Packet<T> implements Serializable {
    private final T data;

    public Packet(T data) {
        this.data = data;
    }

    public T get() {
        return data;
    }
}
