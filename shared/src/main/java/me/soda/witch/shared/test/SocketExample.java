package me.soda.witch.shared.test;

import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SocketExample {
    public static void main(String[] args) throws Exception {
        Crypto.INSTANCE = new Crypto(new byte[]{0, 1});
        Server server = new Server();
        //server.start(23444);
        new Client("127.0.0.1", 11451, 1000);


        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = inputStream.readLine();
            String[] msgArr = in.split(" ");
            if (msgArr.length > 0) {
                try {
                    switch (msgArr[0]) {
                        case "stop" -> server.stop();
                        case "conn" ->
                                server.getConnections().forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT));
                        case "cc" ->
                                server.getConnections().forEach(connection -> connection.close(DisconnectData.Reason.NOREC));
                        default -> //if (in.equals("qq"))
                            //    server.getConnections().forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT));
                            //if (in.equals("zz")) server.stop();
                            //client.send(new Message("em-client", in));
                                server.getConnections().forEach(connection -> connection.send(
                                        Message.fromList(
                                                "13",
                                                List.of(
                                                        Message.fromString("hello"),
                                                        Message.fromString("sdf"),
                                                        Message.fromList(
                                                                "test",
                                                                List.of(
                                                                        Message.fromBoolean("test", true)
                                                                )
                                                        )
                                                )
                                        )
                                ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Server extends TcpServer {
        public Server() throws IOException {
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
        public void onClose(Connection connection, DisconnectData packet) {
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
            send(Message.fromString("1", "resp"));
        }

        @Override
        public void onClose(DisconnectData disconnectData) {
            System.out.println("close" + this + disconnectData);
        }

        @Override
        public boolean onReconnect() {
            System.out.println("rec" + this);
            return true;
        }
    }
}
