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
    public static final int BUF_SIZE = 65535;
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
        } catch (IOException e) {
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

    public void close(DisconnectInfo.Reason reason) {
        close(new DisconnectInfo(reason, "default"));
    }

    public void close(DisconnectInfo info) {
        send(new Message("disconnect", info));
        // Wait server to close client
        if (this instanceof TcpClient) forceClose();
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

    public void send(Message data) {
        if (!isConnected()) return;
        try {
            String str = data.serialize();
            for (int i = 1; i < str.length() / BUF_SIZE + 2; i++) {
                out.writeUTF(str.substring(BUF_SIZE * (i - 1), Math.min(BUF_SIZE * i, str.length())));
            }
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }
    }

    public Message read() throws IOException {
        String str = in.readUTF();
        StringBuilder sb = new StringBuilder();
        while (str.length() >= BUF_SIZE) {
            sb.append(str);
            str = in.readUTF();
        }
        sb.append(str);
        Message message = Message.deserialize(sb.toString());
        if (message.data instanceof DisconnectInfo info) this.disconnectInfo = info;
        return message;
    }

    public boolean isConnected() {
        return socket.isConnected() || !socket.isClosed() || disconnectInfo == EXCEPTION;
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    public DisconnectInfo getDisconnectInfo() {
        return disconnectInfo;
    }
}
