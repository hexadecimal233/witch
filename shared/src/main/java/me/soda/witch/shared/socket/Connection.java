package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Connection implements Runnable {
    public static final DisconnectInfo EXCEPTION = new DisconnectInfo(DisconnectInfo.Reason.EXCEPTION, "");
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private DisconnectInfo disconnectInfo;

    public Connection(Socket socket) throws IOException {
        connect(socket);
    }

    public Connection() {
    }

    @Override
    public void run() {
        try {
            onOpen();
            while (isConnected()) {
                Message message = read();
                if (message.data instanceof DisconnectInfo info) {
                    close(info);
                    break;
                } else {
                    onMessage(message);
                }
            }
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        } finally {
            onClose(getDisconnectInfo());
            afterClose(getDisconnectInfo());
        }
    }

    public abstract void onOpen();

    public abstract void onMessage(Message message);

    public abstract void onClose(DisconnectInfo disconnectInfo);

    public void afterClose(DisconnectInfo disconnectInfo) {

    }

    public void connect(Socket socket) throws IOException {
        disconnectInfo = null;
        this.socket = socket;
        initIO();
    }

    private void initIO() throws IOException {
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        disconnectInfo = EXCEPTION;
    }

    public void forceClose() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }
    }

    public void close(DisconnectInfo.Reason reason) {
        close(new DisconnectInfo(reason, "default"));
    }

    public void close(DisconnectInfo info) {
        if (isConnected()) {
            send(new Message("disconnect", info));
        }
        forceClose();
    }

    public void send(Message data) {
        try {
            out.writeUTF(data.serialize());
            out.flush();
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
    }

    public Message read() throws IOException {
        Message message = Message.deserialize(in.readUTF());
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
