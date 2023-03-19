package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;
import me.soda.witch.shared.socket.messages.messages.OKData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Connection implements Runnable {
    public static final DisconnectData EXCEPTION = new DisconnectData(DisconnectData.Reason.EXCEPTION, "");
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private DisconnectData disconnectData;
    private boolean reallyConnected = false;

    public Connection(Socket socket) throws IOException {
        connect(socket);
    }

    public Connection() {
    }

    @Override
    public void run() {
        try {
            send(new OKData());
            while (isConnected()) {
                Message message = read();
                if (message == null) continue;
                if (!reallyConnected && message.data instanceof OKData) {
                    reallyConnected = true;
                    onOpen();
                } else if (reallyConnected) {
                    if (message.data instanceof DisconnectData info) {
                        disconnectData = info;
                        close(info);
                        break;
                    } else {
                        onMessage(message);
                    }
                } else forceClose();
            }
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        } finally {
            if (reallyConnected) {
                reallyConnected = false;
                onClose(getDisconnectInfo());
                afterClose(getDisconnectInfo());
            }
        }
    }

    public abstract void onOpen();

    public abstract void onClose(DisconnectData disconnectData);

    public abstract void onMessage(Message message);

    public void afterClose(DisconnectData disconnectData) {
    }

    public void connect(Socket socket) throws IOException {
        disconnectData = null;
        this.socket = socket;
        initIO();
    }

    private void initIO() throws IOException {
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        disconnectData = EXCEPTION;
    }

    public void close(DisconnectData.Reason reason) {
        close(new DisconnectData(reason, "default"));
    }

    public void close(DisconnectData info) {
        send(info);
        // Wait server to close client
        if (this instanceof TcpClient) forceClose();
    }

    public void forceClose() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }
    }

    public void send(Data data) {
        send(new Message(data));
    }

    private void send(Message data) {
        if (!isConnected()) return;
        try {
            byte[] encryptedData = data.encrypt();
            out.writeInt(encryptedData.length);
            out.write(encryptedData);
        } catch (Exception e) { // JsonParseException & IOException
            LogUtil.printStackTrace(e);
        }
    }

    public Message read() throws IOException {
        int length = in.readInt();
        byte[] encryptedData = new byte[length];
        in.readFully(encryptedData);
        try {
            return Message.decrypt(encryptedData);
        } catch (Exception e) { // JsonParseException
            return null;
        }
    }

    public boolean isConnected() {
        return socket.isConnected() || !socket.isClosed() || disconnectData == EXCEPTION;
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    public DisconnectData getDisconnectInfo() {
        return disconnectData;
    }
}
