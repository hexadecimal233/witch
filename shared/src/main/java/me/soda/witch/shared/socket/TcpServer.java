package me.soda.witch.shared.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpServer {
    private final ServerSocket server;
    private final HashSet<Connection> conns = new HashSet<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public TcpServer(int port) throws Exception {
        server = new ServerSocket(port);
        new ServerThread().start();
    }

    public HashSet<Connection> getConnections() {
        return conns;
    }

    public void stop() throws IOException {
        conns.forEach(connection -> connection.close(DisconnectInfo.Reason.NORMAL));
        pool.shutdown();
        server.close();
    }

    public boolean isStopped() {
        return server.isClosed();
    }

    public abstract void onOpen(Connection connection);

    public abstract void onClose(Connection connection, DisconnectInfo packet);

    public abstract void onMessage(Connection connection, Message message);

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();
                    pool.execute(new ClientHandler(socket));
                } catch (Exception ignored) {
                }
            }
        }
    }

    private class ClientHandler implements Runnable {
        private final Connection conn;

        public ClientHandler(Socket socket) throws IOException {
            this.conn = new Connection(socket);
        }

        @Override
        public void run() {
            try {
                conns.add(conn);
                onOpen(conn);
                while (conn.isConnected()) {
                    Message message = conn.read();
                    if (!(message.data instanceof DisconnectInfo info)) {
                        onMessage(conn, message);
                    } else {
                        conn.close(info);
                        break;
                    }
                }
            } catch (Exception ignored) {
            } finally {
                DisconnectInfo di = conn.getDisconnectInfo();
                if (di == null) di = new DisconnectInfo(DisconnectInfo.Reason.EXCEPTION, "");
                onClose(conn, di);
                conns.remove(conn);
            }
        }
    }
}
