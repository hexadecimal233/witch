package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpClient extends Connection {
    private final String host;
    private final int port;
    private final ExecutorService reconnectExecutor = Executors.newSingleThreadExecutor();
    private long reconnectTimeout;

    public TcpClient(String host, int port, long reconnectTimeout) {
        super();
        this.reconnectTimeout = reconnectTimeout;
        this.host = host;
        this.port = port;
        try {
            connect(new Socket(host, port));
        } catch (Exception e) {
            reconnect(false);
        }
    }

    public void setReconnectTimeout(int reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }

    private void reconnect(boolean noTimeout) {
        if ((!onReconnect() || reconnectTimeout <= 0) && !reconnectExecutor.isShutdown()) {
            reconnectExecutor.shutdown();
            return;
        }
        reconnectExecutor.execute(() -> {
            try {
                if (!noTimeout) Thread.sleep(reconnectTimeout);
                connect(new Socket(host, port));
            } catch (Exception e) {
                LogUtil.printStackTrace(e);
                reconnect(noTimeout);
            }
        });
    }

    public boolean onReconnect() {
        return true;
    }

    @Override
    public void onClose(DisconnectInfo disconnectInfo) {
        boolean reconnectTimeout = false;
        switch (disconnectInfo.reason()) {
            case NO_RECONNECT -> setReconnectTimeout(-1);
            case RECONNECT -> reconnectTimeout = true;
        }
        reconnect(reconnectTimeout);
    }
}
