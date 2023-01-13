package me.soda.witch.shared.test;

import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SocketExample {
    public static void main(String[] args) throws Exception {
        Server server = new Server(11451);
        Client client = new Client("localhost", 11451, 1000);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = inputStream.readLine();
            String[] msgArr = in.split(" ");
            if (msgArr.length > 0) {
                try {
                    switch (msgArr[0]) {
                        case "stop" -> server.stop();
                        case "conn" ->
                                server.getConnections().forEach(connection -> connection.close(DisconnectInfo.Reason.RECONNECT));
                        case "cc" ->
                                server.getConnections().forEach(connection -> connection.close(DisconnectInfo.Reason.NO_RECONNECT));
                        default -> {
                            if (in.equals("qq"))
                                server.getConnections().forEach(connection -> connection.close(DisconnectInfo.Reason.RECONNECT));
                            if (in.equals("zz")) server.stop();
                            server.getConnections().forEach(connection -> connection.send(new Message("em", in)));
                            //client.send(new Message("em-client", in));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            send(new Message("1", "resp"));
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
