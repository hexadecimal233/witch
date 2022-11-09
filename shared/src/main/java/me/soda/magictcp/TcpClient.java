package me.soda.magictcp;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpClient extends Connection {
    private SocketThread socketThread;
    private long reconnectTimeout;
    private final String[] addrPort;
    private final ExecutorService reconnectPool = Executors.newFixedThreadPool(1);

    public TcpClient(String address, long reconnectTimeout) {
        super();
        this.addrPort = address.split(":");
        this.reconnectTimeout = reconnectTimeout;
        try {
            connect(new Socket(addrPort[0], Integer.parseInt(addrPort[1])));
            socketThread = new SocketThread();
            socketThread.start();
        } catch (Exception e) {
            reconnect();
        }
    }

    //-1 means no
    public void setReconnectTimeout(int reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }

    private void reconnect() {
        if ((!onReconnect() || reconnectTimeout <= 0) && !reconnectPool.isShutdown()) {
            reconnectPool.shutdown();
            return;
        }
        reconnectPool.execute(()->{
            try {
                Thread.sleep(reconnectTimeout);
                connect(new Socket(addrPort[0], Integer.parseInt(addrPort[1])));
                socketThread = new SocketThread();
                socketThread.start();
            } catch (Exception e) {
                reconnect();
            }
        });
    }

    public abstract boolean onReconnect();

    public abstract void onOpen();

    public abstract void onClose();

    public abstract void onMessage(Object o);

    private class SocketThread extends Thread {
        @Override
        public void run() {
            try {
                onOpen();
                while (isConnected()) {
                    onMessage(read());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                onClose();
                close();
                reconnect();
            }
        }
    }
}
