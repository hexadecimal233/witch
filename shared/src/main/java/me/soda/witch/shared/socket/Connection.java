package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DisconnectInfo disconnectInfo;

    public Connection(Socket socket) throws IOException {
        connect(socket);
    }

    public Connection() {
    }

    public void connect(Socket socket) throws IOException {
        disconnectInfo = null;
        this.socket = socket;
        initIO();
    }

    private void initIO() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void forceClose() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
    }

    public void close(DisconnectInfo.Reason reason) {
        close(new DisconnectInfo(reason, "message"));
    }

    public void close(DisconnectInfo info) {
        if (isConnected())
            send(new Message("disconnect", info));
        else
            forceClose();
    }

    public void send(Message data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
    }

    public Message read() throws Exception {
        Message message = (Message) in.readObject();
        if (message.data instanceof DisconnectInfo info) this.disconnectInfo = info;
        return message;
    }

    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    public DisconnectInfo getDisconnectInfo() {
        return disconnectInfo;
    }
}
