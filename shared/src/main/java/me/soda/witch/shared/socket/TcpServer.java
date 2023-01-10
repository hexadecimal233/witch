package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

public abstract class TcpServer {
    private final ServerSocket server;
    private final HashSet<Connection> conns = new HashSet<>();

    public TcpServer(int port) throws IOException {
        server = new ServerSocket(port);
        new ServerThread().start();
    }

    public HashSet<Connection> getConnections() {
        return conns;
    }

    public void stop() throws IOException {
        conns.forEach(connection -> connection.close(DisconnectInfo.Reason.NORMAL));
        server.close();
    }

    public boolean isStopped() {
        return server.isClosed();
    }

    public abstract void onOpen(Connection connection);

    public abstract void onMessage(Connection connection, Message message);

    public abstract void onClose(Connection connection, DisconnectInfo packet);

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while (!server.isClosed()) {
                try {
                    new Connection(server.accept()) {
                        @Override
                        public void onOpen() {
                            conns.add(this);
                            TcpServer.this.onOpen(this);
                        }

                        @Override
                        public void onMessage(Message message) {
                            TcpServer.this.onMessage(this, message);
                        }

                        @Override
                        public void onClose(DisconnectInfo disconnectInfo) {
                            conns.remove(this);
                            TcpServer.this.onClose(this, disconnectInfo);
                        }
                    };
                } catch (Exception e) {
                    LogUtil.printStackTrace(e);
                }
            }
        }
    }
}
