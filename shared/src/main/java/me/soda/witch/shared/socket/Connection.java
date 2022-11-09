package me.soda.witch.shared.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Connection(Socket socket) throws IOException {
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

    public void send(Object object) {
        try {
            out.writeObject(object);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object read() throws Exception {
        return in.readObject();
    }

    public byte[] readBytes() throws Exception {
        return (byte[]) read();
    }

    public boolean isConnected() {
        return socket.isConnected() || !socket.isClosed();
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }
}
