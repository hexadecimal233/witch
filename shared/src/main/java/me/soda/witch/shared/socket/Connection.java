package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Connection {
    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
    public final Runnable SOCKET_HANDLER = () -> {
        try {
            onOpen();
            while (isConnected()) {
                Message message = read();
                if (!(message.data instanceof DisconnectInfo info)) {
                    onMessage(message);
                } else {
                    close(info);
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        } finally {
            DisconnectInfo di = getDisconnectInfo();
            onClose(di);
        }
    };

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DisconnectInfo disconnectInfo;

    public Connection(Socket socket) throws IOException {
        connect(socket);
    }

    public Connection() {
    }

    public abstract void onOpen();

    public abstract void onMessage(Message message);

    public abstract void onClose(DisconnectInfo disconnectInfo);

    public void connect(Socket socket) throws IOException {
        disconnectInfo = null;
        this.socket = socket;
        initIO();
        connectionExecutor.execute(SOCKET_HANDLER);
    }

    private void initIO() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        disconnectInfo = new DisconnectInfo(DisconnectInfo.Reason.EXCEPTION, "");
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
        close(new DisconnectInfo(reason, ""));
    }

    public void close(DisconnectInfo info) {
        if (isConnected()) {
            send(new Message("disconnect", info));
            forceClose();
        }
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
