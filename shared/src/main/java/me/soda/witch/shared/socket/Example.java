package me.soda.witch.shared.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Example {
    public static void main(String[] args) throws Exception {
        Server server = new Server(11451);
        new Client("localhost:11451");
        new Client("127.0.0.1:11451");
        new Client("127.0.0.1:11451");
        new Client("127.0.0.1:11451");
        new Client("127.0.0.1:11451");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (!server.isStopped()) {
            String in = inputStream.readLine();
            server.getConnections().forEach(connection -> connection.send(in.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public static class Server extends TcpServer {

        public Server(int port) throws Exception {
            super(port);
        }

        @Override
        public void onOpen(Connection connection) {
            System.out.println("openeds");
        }

        @Override
        public void onClose(Connection connection) {
            System.out.println("closeds");
        }

        @Override
        public void onMessage(Connection connection, byte[] bytes) {
            String str = new String(bytes);
            System.out.println("server:" + bytes.length);
        }
    }

    public static class Client extends TcpClient {
        public Client(String address) throws Exception {
            super(address);
        }

        @Override
        public void onOpen() {
            System.out.println("opened");
        }

        @Override
        public void onClose() {
            System.out.println("closed");
        }

        @Override
        public void onMessage(byte[] bytes) {
            String str = new String(bytes);
            System.out.println("client:" + bytes.length);
            send(str.getBytes(StandardCharsets.UTF_8));
        }
    }
}
