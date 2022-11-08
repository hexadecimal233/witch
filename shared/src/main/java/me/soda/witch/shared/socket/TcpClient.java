package me.soda.witch.shared.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class TcpClient extends Connection {
    private SocketThread socketThread;

    public TcpClient(String address) throws Exception {
        super(new Socket(address.split(":")[0], Integer.parseInt(address.split(":")[1])));
        initIOStream();
    }

    @Override
    public void initIOStream() throws IOException {
        super.initIOStream();
        socketThread = new SocketThread();
        socketThread.start();
    }

    public void reconnect() {
        try {
            onClose();
            close();
            socketThread.wait(1000);
            initIOStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onOpen();

    public abstract void onClose();

    public abstract void onMessage(byte[] bytes);

    private class SocketThread extends Thread {
        @Override
        public void run() {
            try {
                onOpen();
                byte[] buffer = new byte[65535];
                int size;
                while (isConnected()) {
                    if ((size = in.read(buffer)) != -1) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(buffer, 0, size);
                        onMessage(os.toByteArray());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
