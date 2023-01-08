package me.soda.witch.shared.socket.example;

import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.Packet;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.TcpServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Example {
    public static void main(String[] args) throws Exception {
        Server server = new Server(11451);
        new Thread(() -> {
            try {
                new Client("localhost:11451");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (!server.isStopped()) {
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
                            server.getConnections().forEach(connection -> connection.close(Packet.DisconnectPacket.Reason.RECONNECT));
                    default -> server.getConnections().forEach(connection -> connection.send(in));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Server extends TcpServer {

        public Server(int port) throws Exception {
            super(port, true);
        }

        @Override
        public void onOpen(Connection connection) {
            System.out.println("openeds");
        }

        @Override
        public void onClose(Connection conn, Packet.DisconnectPacket disconnectPacket) {
            System.out.println("closeds");
        }

        @Override
        public void onMessage(Connection connection, Object o) {
            String str = (String) o;
            System.out.println("server:" + str);
        }
    }

    public static class Client extends TcpClient {
        public Client(String address) {
            super(address, 1000, true);
        }

        @Override
        public boolean onReconnect() {
            System.out.println("reco");
            return true;
        }

        @Override
        public void onOpen() {
            System.out.println("opened");
        }

        @Override
        public void onClose(Packet.DisconnectPacket packet) {
            System.out.println("closed");
        }

        @Override
        public void onMessage(Object o) {
            String str = (String) o;
            System.out.println("client:" + str);
            send(str);
        }
    }
}
