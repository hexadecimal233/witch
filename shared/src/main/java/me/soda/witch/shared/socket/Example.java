package me.soda.witch.shared.socket;

import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Example {
    public static void main(String[] args) throws Exception {
        Server server = new Server(11451);
        new Thread(() -> {
            try {
                new Client("localhost", 11451, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = inputStream.readLine();
            handle(in, server);
        }
    }

    public static void handle(String in, Server server) {
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop" -> server.stop();
                    case "conn" ->
                            server.getConnections().forEach(connection -> connection.close(DisconnectInfo.Reason.RECONNECT));
                    case "cc" ->
                            server.getConnections().forEach(connection -> connection.close(DisconnectInfo.Reason.NO_RECONNECT));
                    default -> server.getConnections().forEach(connection -> connection.send(new Message("em", in)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Server extends TcpServer {
        public Server(int port) throws Exception {
            super(port);
        }

        @Override
        public void onOpen(Connection connection) {
            System.out.println("open" + this);
        }

        @Override
        public void onMessage(Connection connection, Message message) {
            System.out.println("message" + this + message);
        }

        @Override
        public void onClose(Connection connection, DisconnectInfo packet) {
            System.out.println("close" + this + packet);
        }
    }

    public static class Client extends TcpClient {
        public Client(String host, int port, long reconnectTimeout) {
            super(host, port, reconnectTimeout);
        }

        @Override
        public void onOpen() {
            System.out.println("open" + this);
        }

        @Override
        public void onMessage(Message message) {
            System.out.println("message" + message);
            send(new Message("1", ""));
        }

        @Override
        public void onClose(DisconnectInfo disconnectInfo) {
            System.out.println("close" + this + disconnectInfo);
        }

        @Override
        public boolean onReconnect() {
            System.out.println("rec" + this);
            return true;
        }
    }
}
