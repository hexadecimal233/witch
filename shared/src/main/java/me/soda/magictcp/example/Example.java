package me.soda.magictcp.example;

import me.soda.magictcp.Connection;
import me.soda.magictcp.TcpClient;
import me.soda.magictcp.TcpServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Example {
    public static void main(String[] args) throws Exception {
        Server server = new Server(11452);
        new Thread(()-> {
            try {
                new Client("localhost:11451");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (!server.isStopped()) {
            String in = inputStream.readLine();
            if (in.equals("q")) {
                server.stop();
            } else
                server.getConnections().forEach(connection -> connection.send(in));
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
        public void onMessage(Connection connection, Object o) {
            String str = (String) o;
            System.out.println("server:" + str);
        }
    }

    public static class Client extends TcpClient {
        public Client(String address) throws Exception {
            super(address,1000);
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
        public void onClose() {
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
