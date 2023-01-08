package me.soda.witch.shared.socket;

import java.io.EOFException;
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
    private final boolean compress;

    public TcpServer(int port, boolean compress) throws Exception {
        this.compress = compress;
        server = new ServerSocket(port);
        new ServerThread().start();
    }

    public HashSet<Connection> getConnections() {
        return conns;
    }

    public void stop() throws IOException {
        pool.shutdown();
        conns.forEach(connection -> connection.close(Packet.DisconnectPacket.Reason.NORMAL));
        server.close();
    }

    public boolean isStopped() {
        return server.isClosed();
    }

    public abstract void onOpen(Connection connection);

    public abstract void onClose(Connection connection, Packet.DisconnectPacket packet);

    public abstract <T> void onMessage(Connection connection, T t);

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();
                    pool.execute(new ClientHandler(socket));
                } catch (Exception e) {
                    //ignored
                }
            }
        }
    }

    private class ClientHandler implements Runnable {
        private final Connection conn;

        public ClientHandler(Socket socket) throws IOException {
            this.conn = new Connection(socket, compress);
        }

        @Override
        public void run() {
            try {
                conns.add(conn);
                onOpen(conn);
                while (conn.isConnected()) {
                    Object obj = conn.read(Object.class);
                    if (!(obj instanceof Packet.DisconnectPacket)) {
                        onMessage(conn, obj);
                    }
                }
            } catch (EOFException e) {
                //ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                onClose(conn, conn.getDisconnectPacket());
                conns.remove(conn);
                conn.forceClose();
            }
        }
    }
}
