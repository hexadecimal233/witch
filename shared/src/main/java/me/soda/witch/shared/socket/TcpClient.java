package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class TcpClient extends Connection {
    private final String host;
    private final int port;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService connectExecutor = Executors.newSingleThreadExecutor();
    public long reconnectTimeout;

    public TcpClient(String host, int port, long reconnectTimeout) {
        super();
        this.reconnectTimeout = reconnectTimeout;
        this.host = host;
        this.port = port;
        try {
            connect(new Socket(host, port));
            connectExecutor.execute(this);
        } catch (IOException e) {
            reconnect(false);
        }
    }

    private void reconnect(boolean noTimeout) {
        if ((!onReconnect() || reconnectTimeout <= 0) && !reconnectExecutor.isShutdown()) {
            reconnectExecutor.shutdown();
            return;
        }
        reconnectExecutor.schedule(() -> {
            try {
                connect(new Socket(host, port));
                connectExecutor.execute(this);
            } catch (IOException e) {
                LogUtil.printStackTrace(e);
                reconnect(noTimeout);
            }
        }, noTimeout ? 0 : reconnectTimeout, TimeUnit.MILLISECONDS);
    }

    public boolean onReconnect() {
        return true;
    }

    @Override
    public void afterClose(DisconnectData disconnectData) {
        boolean instaReconnect = false;
        switch (disconnectData.reason()) {
            case NOREC -> this.reconnectTimeout = -1;
            case RECONNECT -> instaReconnect = true;
        }
        reconnect(instaReconnect);
    }
}
