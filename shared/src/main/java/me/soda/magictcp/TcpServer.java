package me.soda.magictcp;

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
        pool.shutdown();
        server.close();
    }

    public boolean isStopped() {
        return server.isClosed();
    }

    public abstract void onOpen(Connection connection);

    public abstract void onClose(Connection connection);

    public abstract void onMessage(Connection connection, Object o);

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();
                    pool.execute(new ClientHandler(socket));
                } catch (Exception e) {
                    e.printStackTrace();
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
                    onMessage(conn, conn.read());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                onClose(conn);
                conns.remove(conn);
                conn.close();
            }
        }
    }
}
