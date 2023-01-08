package me.soda.witch.shared.socket;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpClient extends Connection {
    private final String[] addrPort;
    private final ExecutorService reconnectPool = Executors.newFixedThreadPool(1);
    private SocketThread socketThread;
    private long reconnectTimeout;

    public TcpClient(String address, long reconnectTimeout, boolean compress) {
        super(compress);
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

    //-1 means no
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

    public abstract boolean onReconnect();

    public abstract void onOpen();

    public abstract void onClose(Packet.DisconnectPacket packet);

    public abstract <T> void onMessage(T t);

    private class SocketThread extends Thread {
        @Override
        public void run() {
            try {
                onOpen();
                while (isConnected()) {
                    Object obj = read(Object.class);
                    if (!(obj instanceof Packet.DisconnectPacket)) {
                        onMessage(obj);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Packet.DisconnectPacket dp = getDisconnectPacket();
                boolean reconnectTimeout = false;
                onClose(dp);
                switch (dp.reason) {
                    case NO_RECONNECT -> setReconnectTimeout(-1);
                    case RECONNECT -> reconnectTimeout = true;
                }
                forceClose();
                reconnect(reconnectTimeout);
            }
        }
    }
}
