package me.soda.magictcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Connection(Socket socket) throws IOException {
        connect(socket);
    }

    public Connection() {

    }

    public void connect(Socket socket) throws IOException {
        this.socket = socket;
        initIO();
    }

    public void initIO() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void send(T data) {
        try {
            out.writeObject(new Packet<>(data));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T read() throws Exception {
        Packet<T> packet = (Packet<T>) in.readObject();
        return packet.get();
    }

    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }
}
