package me.soda.witch.shared.socket;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpClient extends Connection {
    private final String[] addrPort;
    private final ExecutorService reconnectPool = Executors.newSingleThreadExecutor();
    private SocketThread socketThread;
    private long reconnectTimeout;

    public TcpClient(String address, long reconnectTimeout) {
        super();
        this.addrPort = address.split(":");
        this.reconnectTimeout = reconnectTimeout;
        try {
            connect(new Socket(addrPort[0], Integer.parseInt(addrPort[1])));
            socketThread = new SocketThread();
            socketThread.start();
        } catch (Exception e) {
            reconnect(false);
        }
    }

    public void setReconnectTimeout(int reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }

    private void reconnect(boolean noTimeout) {
        if ((!onReconnect() || reconnectTimeout <= 0) && !reconnectPool.isShutdown()) {
            reconnectPool.shutdown();
            return;
        }
        reconnectPool.execute(() -> {
            try {
                if (!noTimeout) Thread.sleep(reconnectTimeout);
                connect(new Socket(addrPort[0], Integer.parseInt(addrPort[1])));
                socketThread = new SocketThread();
                socketThread.start();
            } catch (Exception e) {
                reconnect(noTimeout);
            }
        });
    }

    public abstract void onOpen();

    public abstract void onMessage(Message message);

    public abstract void onClose(DisconnectInfo disconnectInfo);

    public boolean onReconnect() {
        return true;
    }

    private class SocketThread extends Thread {
        @Override
        public void run() {
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
            } catch (Exception ignored) {
            } finally {
                DisconnectInfo di = getDisconnectInfo();
                if (di == null) di = new DisconnectInfo(DisconnectInfo.Reason.EXCEPTION, "");
                boolean reconnectTimeout = false;
                onClose(di);
                switch (di.reason()) {
                    case NO_RECONNECT -> setReconnectTimeout(-1);
                    case RECONNECT -> reconnectTimeout = true;
                }
                reconnect(reconnectTimeout);
            }
        }
    }
}
