package me.soda.witch.shared.socket;

import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpServer {
    private final ServerSocket server;
    private final HashSet<Connection> conns = new HashSet<>();
    private final ExecutorService connectionThreadPool = Executors.newCachedThreadPool();

    public TcpServer(int port) throws IOException {
        server = new ServerSocket(port);
        new ServerThread().start();
    }

    public HashSet<Connection> getConnections() {
        return conns;
    }

    public void stop() throws IOException {
        conns.forEach(connection -> connection.close(DisconnectInfo.Reason.NORMAL));
        connectionThreadPool.shutdown();
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
                    connectionThreadPool.execute(new ServerConnection(server.accept()));
                } catch (IOException e) {
                    LogUtil.printStackTrace(e);
                }
            }
        }
    }

    private class ServerConnection extends Connection {
        public ServerConnection(Socket socket) throws IOException {
            super(socket);
        }

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
    }
}
