package me.soda.witch.shared.socket;

import java.net.Socket;

public abstract class TcpClient extends Connection {
    private SocketThread socketThread;

    public TcpClient(String address) throws Exception {
        super(new Socket(address.split(":")[0], Integer.parseInt(address.split(":")[1])));
        socketThread = new SocketThread();
        socketThread.start();
    }

    public void reconnect() {
        //todo
        //try {
        //    onClose();
        //    close();
        //    socketThread.wait();
        //    initIO();
        //    socketThread.start();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public abstract void onOpen();

    public abstract void onClose();

    public abstract void onMessage(byte[] bytes);

    private class SocketThread extends Thread {
        @Override
        public void run() {
            try {
                onOpen();
                while (isConnected()) {
                    onMessage(readBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                onClose();
                close();
            }
        }
    }
}
